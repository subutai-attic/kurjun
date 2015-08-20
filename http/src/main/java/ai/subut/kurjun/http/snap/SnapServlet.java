package ai.subut.kurjun.http.snap;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.http.ServletUtils;
import ai.subut.kurjun.model.metadata.snap.SnapMetadata;
import ai.subut.kurjun.model.metadata.snap.SnapMetadataFilter;
import ai.subut.kurjun.model.metadata.snap.SnapMetadataStore;
import ai.subut.kurjun.model.metadata.snap.SnapUtils;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.snap.service.SnapMetadataParser;


@Singleton
@MultipartConfig
class SnapServlet extends HttpServlet
{

    private static final Logger LOGGER = LoggerFactory.getLogger( SnapServlet.class );

    private static final String SNAPS_PATH = "snaps";
    private static final String SNAPS_PACKAGE_PART = "package";
    private static final String SNAPS_MD5_PARAM = "md5";
    private static final String SNAPS_NAME_PARAM = "name";
    private static final String SNAPS_VERSION_PARAM = "version";

    @Inject
    private SnapMetadataParser metadataParser;

    @Inject
    private SnapMetadataStore metadataStore;

    @Inject
    private FileStore fileStore;


    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        List<String> paths = ServletUtils.splitPath( req.getPathInfo() );
        if ( paths.size() == 1 && paths.get( 0 ).equals( SNAPS_PATH ) )
        {
            String md5 = req.getParameter( SNAPS_MD5_PARAM );
            if ( md5 != null )
            {
                getByMd5( md5, resp );
                return;
            }

            String name = req.getParameter( SNAPS_NAME_PARAM );
            String version = req.getParameter( SNAPS_VERSION_PARAM );
            if ( name != null )
            {
                getByNameAndVersion( name, version, resp );
            }
            else
            {
                String msg = "Neither 'md5' nor 'name' and 'version' params specififed";
                writeResponse( resp, HttpServletResponse.SC_BAD_REQUEST, msg );
            }
        }
        else
        {
            writeResponse( resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path: " + req.getPathInfo() );
        }
    }


    @Override
    protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {

        ServletUtils.setMultipartConfig( req, this.getClass() );

        req.setAttribute( Request.__MULTIPART_CONFIG_ELEMENT, new MultipartConfigElement( "" ) );
        if ( ServletUtils.isMultipart( req ) )
        {
            List<String> paths = ServletUtils.splitPath( req.getPathInfo() );

            if ( paths.size() == 1 && paths.get( 0 ).equals( SNAPS_PATH ) )
            {
                Part part = req.getPart( SNAPS_PACKAGE_PART );
                if ( part != null )
                {
                    parsePackageFile( part, resp );
                }
                else
                {
                    String msg = String.format( "No package file attached with name '%s'", SNAPS_PACKAGE_PART );
                    writeResponse( resp, HttpServletResponse.SC_BAD_REQUEST, msg );
                }
            }
        }
        else
        {
            writeResponse( resp, HttpServletResponse.SC_BAD_REQUEST, "Request is not a multipart request" );
        }
    }


    private void getByMd5( String md5, HttpServletResponse resp ) throws IOException
    {
        try
        {
            byte[] md5bytes = Hex.decodeHex( md5.toCharArray() );
            if ( metadataStore.contains( md5bytes ) )
            {
                streamPackage( metadataStore.get( md5bytes ), resp );
            }
            else
            {
                writeResponse( resp, HttpServletResponse.SC_NOT_FOUND, "Package not found in metadata store" );
            }
        }
        catch ( DecoderException ex )
        {
            LOGGER.info( "Invalid md5 provided: {}", md5, ex );
            writeResponse( resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid MD5 checksum" );
        }
    }


    private void getByNameAndVersion( String name, String version, HttpServletResponse resp ) throws IOException
    {

        List<SnapMetadata> ls = metadataStore.list( SnapMetadataFilter.getNameFilter( name ) );
        // filter by version if specified
        if ( version != null )
        {
            Iterator<SnapMetadata> it = ls.iterator();
            while ( it.hasNext() )
            {
                if ( !it.next().getVersion().equals( version ) )
                {
                    it.remove();
                }
            }
        }

        if ( ls.isEmpty() )
        {
            writeResponse( resp, HttpServletResponse.SC_NOT_FOUND, "No package(s) found" );
        }
        else if ( ls.size() == 1 )
        {
            streamPackage( ls.get( 0 ), resp );
        }
        else
        {
            String index = makePackagesIndex( ls );
            writeResponse( resp, HttpServletResponse.SC_OK, index );
        }
    }


    private void streamPackage( SnapMetadata meta, HttpServletResponse resp ) throws IOException
    {
        try ( InputStream is = fileStore.get( meta.getMd5() ) )
        {
            if ( is != null )
            {
                resp.setStatus( HttpServletResponse.SC_OK );
                resp.setHeader( "Content-Disposition", "attachment; filename=" + SnapUtils.makeFileName( meta ) );
                IOUtils.copy( is, resp.getOutputStream() );
            }
            else
            {
                writeResponse( resp, HttpServletResponse.SC_NOT_FOUND, "Package not found in file store" );
            }
        }
    }


    private void parsePackageFile( Part part, HttpServletResponse resp ) throws IOException
    {
        byte[] md5 = null;
        SnapMetadata meta;

        // define file extension based on submitted file name
        String fileName = part.getSubmittedFileName();
        String ext = CompressionType.getExtension( fileName );
        if ( ext != null )
        {
            ext = "." + ext;
        }

        Path path = Files.createTempFile( "snap-uplaod", ext );
        try ( InputStream is = part.getInputStream() )
        {
            Files.copy( is, path, StandardCopyOption.REPLACE_EXISTING );

            meta = metadataParser.parse( path.toFile() );
            md5 = fileStore.put( path.toFile() );
        }
        finally
        {
            Files.delete( path );
        }

        if ( Arrays.equals( meta.getMd5(), md5 ) )
        {
            metadataStore.put( meta );
            writeResponse( resp, HttpServletResponse.SC_OK, "Package successfully saved" );
        }
        else
        {
            fileStore.remove( md5 );
            writeResponse( resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Package integrity failure" );
        }
    }


    private void writeResponse( HttpServletResponse resp, int statusCode, String msg ) throws IOException
    {
        resp.setStatus( statusCode );
        try ( ServletOutputStream os = resp.getOutputStream() )
        {
            os.print( msg );
        }
    }


    private String makePackagesIndex( List<SnapMetadata> ls )
    {
        // TODO: maybe better to render html with links to packages
        String delim = ";";
        StringBuilder sb = new StringBuilder();
        sb.append( "count: " ).append( ls.size() ).append( System.lineSeparator() );
        for ( SnapMetadata meta : ls )
        {
            sb.append( "name: " ).append( meta.getName() ).append( delim );
            sb.append( "version: " ).append( meta.getVersion() ).append( delim );
            sb.append( "vendor: " ).append( meta.getName() ).append( delim );
            sb.append( "md5: " ).append( Hex.encodeHexString( meta.getMd5() ) );
            sb.append( System.lineSeparator() );
        }
        return sb.toString();
    }


}

