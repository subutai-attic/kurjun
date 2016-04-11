package ai.subut.kurjun.web.controllers;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.FilenameUtils;

import com.google.common.base.Optional;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.inject.Inject;

import ninja.AssetsController;
import ninja.Context;
import ninja.Renderable;
import ninja.Result;
import ninja.Results;
import ninja.params.PathParam;
import ninja.utils.HttpCacheToolkit;
import ninja.utils.MimeTypes;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;


public class DownloadController {


    private final static Logger LOG = LoggerFactory.getLogger( AssetsController.class );

    public final static String ASSETS_DIR = "assets";

    public final static String FILENAME_PATH_PARAM = "fileName";


    /** Used for dev mode streaming directly from src dir without jetty reload. */
    final String srcDir =
            System.getProperty( "user.dir" ) + File.separator + "src" + File.separator + "main" + File.separator
                    + "java";

    private final String defaultAssetBaseDir;

    private Optional<String> assetBaseDir;

    private final MimeTypes mimeTypes;

    private final HttpCacheToolkit httpCacheToolkit;

    private final NinjaProperties ninjaProperties;


    @Inject
    public DownloadController( HttpCacheToolkit httpCacheToolkit, MimeTypes mimeTypes, NinjaProperties ninjaProperties )
    {

        this.httpCacheToolkit = httpCacheToolkit;
        this.mimeTypes = mimeTypes;
        this.ninjaProperties = ninjaProperties;
        this.assetBaseDir = getNormalizedAssetPath( ninjaProperties );
        this.defaultAssetBaseDir = srcDir + File.separator + ASSETS_DIR + File.separator;
    }


    /**
     * Serves resources from the assets directory of your application.
     *
     * For instance: route: /robots.txt A request to /robots.txt will be served from /assets/robots.txt.
     *
     * You can also use a path like the following to serve files: route: /assets/{fileName: .*}
     *
     * matches /assets/app/app.css and will return /assets/app/app.css (from your jar).
     */
    public Result serveStatic(Context context , @PathParam( "fileName" ) String fileType)
    {
        assetBaseDir = getNormalizedAssetPath( ninjaProperties, fileType );
        Object renderable = new Renderable()
        {

            @Override
            public void render( Context context, Result result )
            {

                String fileName = getFileNameFromPathOrReturnRequestPath( context );

                URL url = getStaticFileFromAssetsDir( context, fileName );

                streamOutUrlEntity( url, context, result );
            }
        };

        return Results.ok().render( renderable );
    }



    private void streamOutUrlEntity( URL url, Context context, Result result )
    {

        // check if stream exists. if not print a notfound exception
        if ( url == null )
        {

            context.finalizeHeadersWithoutFlashAndSessionCookie( Results.notFound() );
        }
        else
        {

            try
            {

                URLConnection urlConnection = url.openConnection();
                Long lastModified = urlConnection.getLastModified();
                httpCacheToolkit.addEtag( context, result, lastModified );

                if ( result.getStatusCode() == Result.SC_304_NOT_MODIFIED )
                {
                    // Do not stream anything out. Simply return 304
                    context.finalizeHeadersWithoutFlashAndSessionCookie( result );
                }
                else
                {

                    result.status( 200 );

                    // Try to set the mimetype:
                    String mimeType = mimeTypes.getContentType( context, url.getFile() );

                    if ( mimeType != null && !mimeType.isEmpty() )
                    {
                        result.contentType( mimeType );
                    }

                    // finalize headers:
                    ResponseStreams responseStreams = context.finalizeHeadersWithoutFlashAndSessionCookie( result );

                    try (
                            InputStream inputStream = urlConnection.getInputStream();
                            OutputStream outputStream = responseStreams.getOutputStream() )
                    {

                        ByteStreams.copy( inputStream, outputStream );
                    }
                }
            }
            catch ( FileNotFoundException e )
            {
                LOG.error( "error streaming file", e );
            }
            catch ( IOException e )
            {
                LOG.error( "error streaming file", e );
            }
        }
    }


    /**
     * Loads files from assets directory. This is the default directory of Ninja where to store stuff. Usually in
     * src/main/java/assets/. But if user wants to use a dir outside of application project dir, then base dir can be
     * overridden by static.asset.base.dir in application conf file.
     */
    private URL getStaticFileFromAssetsDir( Context context, String fileName )
    {

        String finalNameWithoutLeadingSlash = normalizePathWithoutTrailingSlash( fileName );

        Optional<URL> url = Optional.absent();

        //Serve from the static asset base directory specified by user in application conf.
        if ( assetBaseDir.isPresent() )
        {

            String p = assetBaseDir.get();
            String fileSeparator = File.separator;

            File possibleFile = new File( p + fileSeparator + finalNameWithoutLeadingSlash );

            if ( possibleFile.exists() )
            {
                url = getUrlForFile( possibleFile );
            }
        }

        // If asset base dir not specified by user, this allows to directly stream assets from src directory.
        // Therefore jetty does not have to reload. Especially cool when developing js apps inside assets folder.
        if ( ninjaProperties.isDev() && !url.isPresent() )
        {

            File possibleFile = new File( defaultAssetBaseDir + finalNameWithoutLeadingSlash );

            if ( possibleFile.exists() )
            {
                url = getUrlForFile( possibleFile );
            }
        }

        if ( !url.isPresent() )
        {

            // In mode test and prod, if static.asset.base.dir not specified then we stream via the classloader.
            //
            // In dev mode: If we cannot find the file in src we are also looking for the file
            // on the classpath (can be the case for plugins that ship their own assets.
            url = Optional.fromNullable(
                    this.getClass().getClassLoader().getResource( ASSETS_DIR + "/" + finalNameWithoutLeadingSlash ) );
        }

        return url.orNull();
    }


    private Optional<URL> getUrlForFile( File possibleFileInSrc )
    {
        try
        {
            return Optional.fromNullable( possibleFileInSrc.toURI().toURL() );
        }
        catch ( MalformedURLException malformedURLException )
        {

            LOG.error( "Error in dev mode while streaming files from src dir. ", malformedURLException );
        }
        return Optional.absent();
    }



    /**
     * If we get - for whatever reason - a relative URL like assets/../conf/application.conf we expand that to the
     * "real" path. In the above case conf/application.conf.
     *
     * You should then add the assets prefix.
     *
     * Otherwise someone can create an attack and read all resources of our app. If we expand and normalize the incoming
     * path this is no longer possible.
     *
     * @param fileName A potential "fileName"
     *
     * @return A normalized fileName.
     */
    public String normalizePathWithoutTrailingSlash( String fileName )
    {

        // We need simplifyPath to remove relative paths before we process it.
        // Otherwise an attacker can read out arbitrary urls via ".."
        String fileNameNormalized = Files.simplifyPath( fileName );

        if ( fileNameNormalized.charAt( 0 ) == '/' )
        {
            return fileNameNormalized.substring( 1 );
        }

        return fileNameNormalized;
    }

    public static String getFileNameFromPathOrReturnRequestPath( Context context )
    {

        String fileName = context.getPathParameter( FILENAME_PATH_PARAM );

        if ( fileName == null )
        {
            fileName = context.getRequestPath();
        }
        return fileName;
    }


    private Optional<String> getNormalizedAssetPath( NinjaProperties ninjaProperties )
    {
        return Optional.fromNullable( FilenameUtils.normalizeNoEndSeparator( ASSETS_DIR ) );
    }


    private Optional<String> getNormalizedAssetPath( NinjaProperties ninjaProperties, String fileType )
    {
        return Optional.fromNullable( FilenameUtils.normalizeNoEndSeparator( ASSETS_DIR ) );
    }
}
