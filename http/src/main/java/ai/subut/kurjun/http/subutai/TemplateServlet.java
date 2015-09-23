package ai.subut.kurjun.http.subutai;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.http.HttpServer;
import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.http.ServletUtils;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.storage.factory.FileStoreFactory;


@Singleton
class TemplateServlet extends HttpServletBase
{
    public static final String GET_PATH = "get";
    public static final String TYPE_PARAM = "type";
    public static final String RESPONSE_TYPE_MD5 = "md5";

    private static final Logger LOGGER = LoggerFactory.getLogger( TemplateServlet.class );

    @Inject
    private FileStoreFactory fileStoreFactory;

    @Inject
    private PackageMetadataStoreFactory metadataStoreFactory;

    private KurjunContext context;


    @Override
    public void init() throws ServletException
    {
        this.context = HttpServer.CONTEXT;
    }


    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        List<String> pathItems = ServletUtils.splitPath( req.getPathInfo() );
        if ( pathItems.size() == 2 && pathItems.get( 1 ).equals( GET_PATH ) )
        {
            String repo = pathItems.get( 0 );
            String md5 = req.getParameter( MD5_PARAM );
            if ( md5 != null )
            {
                getByMd5( md5, resp );
            }
            else
            {
                String name = req.getParameter( NAME_PARAM );
                String version = req.getParameter( VERSION_PARAM );
                String type = req.getParameter( TYPE_PARAM );
                if ( name != null && type != null )
                {
                    getByNameAndVersion( name, version, type, resp );
                }
                else
                {
                    badRequest( resp, "Name or type patameters not specified" );
                }
            }
        }
        else
        {
            badRequest( resp, "Invalid request" );
        }
    }


    @Override
    protected void doDelete( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        String md5hex = req.getParameter( MD5_PARAM );
        List<String> paths = ServletUtils.splitPath( req.getPathInfo() );

        if ( paths.size() == 1 && md5hex != null )
        {
            String repo = paths.get( 0 );
            byte[] md5;
            try
            {
                md5 = Hex.decodeHex( md5hex.toCharArray() );
            }
            catch ( DecoderException ex )
            {
                LOGGER.warn( ex.getMessage() );
                badRequest( resp, "Invalid md5 value" );
                return;
            }

            PackageMetadataStore metadataStore = metadataStoreFactory.create( context );
            if ( metadataStore.remove( md5 ) )
            {
                FileStore fileStore = fileStoreFactory.create( context );
                fileStore.remove( md5 );
            }
            else
            {
                notFound( resp, "Metadata not found for package md5" );
            }
        }
        else
        {
            badRequest( resp, "Invalid request. Example: /public?md5=..." );
        }
    }


    private void getByMd5( String md5hex, HttpServletResponse resp ) throws IOException
    {
        byte[] md5;
        try
        {
            md5 = Hex.decodeHex( md5hex.toCharArray() );
        }
        catch ( DecoderException ex )
        {
            LOGGER.warn( ex.getMessage() );
            badRequest( resp, "Invalid md5 value" );
            return;
        }

        PackageMetadataStore metadataStore = metadataStoreFactory.create( context );
        SerializableMetadata meta = metadataStore.get( md5 );
        if ( meta == null )
        {
            notFound( resp, "Package not found" );
            return;
        }

        FileStore fileStore = fileStoreFactory.create( context );
        if ( fileStore.contains( md5 ) )
        {
            try ( InputStream is = fileStore.get( md5 ) )
            {
                resp.setHeader( "Content-Disposition", "attachment; filename=" + makeFilename( meta ) );
                IOUtils.copy( is, resp.getOutputStream() );
            }
        }
        else
        {
            notFound( resp, "Package file not found" );
        }
    }


    private void getByNameAndVersion( String name, String version, String type, HttpServletResponse resp ) throws IOException
    {
        Objects.requireNonNull( name, "name parameter" );
        Objects.requireNonNull( type, "type parameter" );

        if ( type.equals( RESPONSE_TYPE_MD5 ) )
        {
            respondMd5( name, version, resp );
        }
        else
        {
            badRequest( resp, "Invalid type parameter. Specify 'md5' to get md5 checksum of packages." );
        }
    }


    private void respondMd5( String name, String version, HttpServletResponse resp ) throws IOException
    {
        PackageMetadataStore metadataStore = metadataStoreFactory.create( context );
        List<SerializableMetadata> items = metadataStore.get( name );
        if ( items.isEmpty() )
        {
            notFound( resp, "No packages found" );
            return;
        }

        SerializableMetadata meta = null;
        if ( version != null )
        {
            for ( SerializableMetadata item : items )
            {
                if ( version.equals( item.getVersion() ) )
                {
                    meta = item;
                    break;
                }
            }
        }
        else
        {
            // alphabetically sort by versions and get latest one
            Optional<SerializableMetadata> m = items.stream().sorted(
                    (m1, m2) -> m1.getVersion().compareTo( m2.getVersion() ) )
                    .skip( items.size() - 1 ).findFirst();
            if ( m.isPresent() )
            {
                meta = m.get();
            }
        }

        if ( meta != null )
        {
            try ( ServletOutputStream os = resp.getOutputStream() )
            {
                os.print( Hex.encodeHexString( meta.getMd5Sum() ) );
            }
        }
        else
        {
            notFound( resp, "Package not found" );
        }
    }


    private String makeFilename( Metadata m )
    {
        return m.getName() + "_" + m.getVersion() + ".tar.gz";
    }


}

