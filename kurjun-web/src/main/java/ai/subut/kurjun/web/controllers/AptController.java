package ai.subut.kurjun.web.controllers;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.DecoderException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.web.handler.SubutaiFileHandler;
import ai.subut.kurjun.web.service.RepositoryService;
import ai.subut.kurjun.web.service.impl.AptManagerServiceImpl;
import ai.subut.kurjun.web.utils.Utils;
import ninja.Context;
import ninja.Renderable;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.session.FlashScope;
import ninja.uploads.FileItem;
import ninja.uploads.FileProvider;


/**
 * Controller for Apt Management
 */
@Singleton
public class AptController extends BaseAptController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AptController.class );

    @Inject
    private AptManagerServiceImpl aptManagerService;

    @Inject
    private RepositoryService repositoryService;


    public Result list( @Param( "type" ) String type, @Param( "repository" ) String repository )
    {
        if ( repository == null )
        {
            repository = "all";
        }

        //********************************************
        //aptManagerService.setUserSession( ( UserSession) context.getAttribute( "USER_SESSION" ) );
        List<SerializableMetadata> serializableMetadataList = aptManagerService.list( repository );
        //********************************************

        return Results.html().template( "views/apts.ftl" ).render( "apts", serializableMetadataList );
    }


    @FileProvider( SubutaiFileHandler.class )
    public Result upload( Context context, @Param( "file" ) FileItem file, FlashScope flashScope ) throws IOException
    {
        try ( InputStream inputStream = new FileInputStream( file.getFile() ) )
        {
            //********************************************
            UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
            URI uri = aptManagerService.upload( uSession, inputStream );
            //********************************************

            if ( uri != null )
            {
                flashScope.success( "File uploaded successfully" );
                return Results.redirect( context.getContextPath() + "/apt" );
            }
        }
        catch ( IOException e )
        {
            LOGGER.error( " ****** Failed to upload apt-file: {}", e.getMessage() );
        }

        flashScope.error( "Failed to upload apt-file" );
        return Results.redirect( context.getContextPath() + "/apt" );
    }


    public Result release( @PathParam( "release" ) String release )
    {
        //        checkNotNull( release, "Release cannot be null" );

        //********************************************
        String rel = aptManagerService.getRelease( release, null, null );
        //********************************************

        if ( rel != null )
        {
            return Results.ok().render( rel ).text();
        }

        return Results.notFound().render( String.format( "Not found:%s", release ) );
    }


    public Result packageIndexes( @PathParam( "release" ) String release, @PathParam( "component" ) String component,
                                  @PathParam( "arch" ) String arch, @PathParam( "packages" ) String packagesIndex )
    {

        //********************************************
        Renderable renderable = aptManagerService.getPackagesIndex( release, component, arch, packagesIndex );
        //********************************************

        return Results.ok().render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
    }


    public Result getPackageByFileName( Context context, @PathParam( "filename" ) String filename )
    {
        //********************************************
        Renderable renderable = aptManagerService.getPackageByFilename( filename );
        //********************************************

        return Results.ok().render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
    }


    public Result info( @Param( "md5" ) String md5, @Param( "name" ) String name, @Param( "version" ) String version )

    {
        //********************************************
        String metadata = null;
        try
        {
            metadata = aptManagerService.getPackageInfo( Utils.MD5.toByteArray( md5 ), name, version );
        }
        catch ( DecoderException e )
        {
            LOGGER.error( " ****** Failed to get info:", e.getMessage() );
        }
        //********************************************

        if ( metadata != null )
        {
            return Results.ok().render( metadata ).text();
        }
        return Results.ok().render( "Not found with details provided" );
    }


    public Result download( @PathParam( "id" ) String md5, FlashScope flashScope )
    {
        //        checkNotNull( md5, "MD5 cannot be null" );

        //********************************************
        Renderable renderable = null;
        try
        {
            renderable = aptManagerService.getPackage( Utils.MD5.toByteArray( md5 ) );
        }
        catch ( DecoderException e )
        {
            LOGGER.error( " ****** Failed to download deb package:", e.getMessage() );
        }
        //********************************************

        if ( renderable != null )
        {
            return Results.ok().render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
        }
        return Results.notFound().text().render( "  ****** Not found with MD5: " + md5 );
    }


    public Result delete( Context context, @PathParam( "id" ) String md5, FlashScope flashScope )
    {
        try
        {
           //********************************************
            UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
            int status = 0;
            status = aptManagerService.delete( uSession, Utils.MD5.toByteArray( md5 ) );
            //********************************************

            switch ( status )
            {
                case 0:
                    flashScope.success( "Deb package removed successfully" );
                    break;
                case 1:
                    flashScope.error( "Deb package was not found " );
                    break;
                case 2:
                    flashScope.error( "Permission denied " );
                    break;
                default:
                    flashScope.error( "Internal Server error " );
                    break;
            }
        }
        catch ( Exception e )
        {
            LOGGER.error( " ****** Failed to delete deb package:", e.getMessage() );
        }


        return Results.redirect( context.getContextPath() + "/apt" );
    }


    public Result md5()
    {
        return Results.ok().render( aptManagerService.md5() ).text();
    }
}
