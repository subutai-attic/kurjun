package ai.subut.kurjun.http.subutai;


import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.http.HttpServer;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.storage.factory.FileStoreFactory;

import static ai.subut.kurjun.http.subutai.TemplateServlet.RESPONSE_TYPE_MD5;


public class HttpServiceImpl implements HttpService
{

    private static final Logger LOGGER = LoggerFactory.getLogger( HttpServiceImpl.class );

    private FileStoreFactory fileStoreFactory;
    private PackageMetadataStoreFactory metadataStoreFactory;


    @Override
    public Response getTemplate( String md5, String name, String version, String type )
    {
        KurjunContext context = getContextForType( type );
        if ( context == null )
        {
            return badRequest( "Invalid template type: " + type );
        }
        try
        {
            if ( md5 != null )
            {
                return getByMd5( md5, context );
            }
            if ( name != null && type != null )
            {
                return getByNameAndVersion( name, version, type, context );
            }
            else
            {
                return badRequest( "Name or type patameters not specified" );
            }
        }
        catch ( IOException ex )
        {
            return Response.serverError().entity( ex ).build();
        }
    }


    @Override
    public Response uploadTemplate( InputStream is )
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public Response deleteTemplates( String md5 )
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }


    /**
     * Gets Kurjun context for templates repository type.
     * <p>
     * TODO: looks for contexts defined in {@link HttpServer} main class. Need mechanism independent of the main class.
     *
     * @param type
     * @return
     */
    protected KurjunContext getContextForType( String type )
    {
        Set<KurjunContext> set = HttpServer.TEMPLATE_CONTEXTS;
        for ( Iterator<KurjunContext> it = set.iterator(); it.hasNext(); )
        {
            KurjunContext c = it.next();
            if ( c.getName().equals( type ) )
            {
                return c;
            }
        }
        return null;
    }


    private Response getByMd5( String md5hex, KurjunContext context ) throws IOException
    {
        byte[] md5;
        try
        {
            md5 = Hex.decodeHex( md5hex.toCharArray() );
        }
        catch ( DecoderException ex )
        {
            LOGGER.warn( ex.getMessage() );
            return badRequest( "Invalid md5 value" );
        }

        PackageMetadataStore metadataStore = metadataStoreFactory.create( context );
        SerializableMetadata meta = metadataStore.get( md5 );
        if ( meta == null )
        {
            return notFound( "Package not found" );
        }

        FileStore fileStore = fileStoreFactory.create( context );
        if ( fileStore.contains( md5 ) )
        {
            try ( InputStream is = fileStore.get( md5 ) )
            {
                return Response.ok( is )
                        .header( "Content-Disposition", "attachment; filename=" + makeFilename( meta ) )
                        .build();
            }
        }
        else
        {
            return notFound( "Package file not found" );
        }
    }


    private Response getByNameAndVersion( String name, String version, String type, KurjunContext context ) throws IOException
    {
        Objects.requireNonNull( name, "name parameter" );
        Objects.requireNonNull( type, "type parameter" );

        if ( type.equals( RESPONSE_TYPE_MD5 ) )
        {
            return respondMd5( name, version, context );
        }
        else
        {
            return badRequest( "Invalid type parameter. Specify 'md5' to get md5 checksum of packages." );
        }
    }


    private Response respondMd5( String name, String version, KurjunContext context ) throws IOException
    {
        PackageMetadataStore metadataStore = metadataStoreFactory.create( context );
        List<SerializableMetadata> items = metadataStore.get( name );
        if ( items.isEmpty() )
        {
            return notFound( "No packages found" );
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
            return Response.ok( Hex.encodeHexString( meta.getMd5Sum() ) ).build();
        }
        else
        {
            return notFound( "Package not found" );
        }
    }


    private String makeFilename( Metadata m )
    {
        return m.getName() + "_" + m.getVersion() + ".tar.gz";
    }


    private Response badRequest( String message )
    {
        return Response.status( Response.Status.BAD_REQUEST ).entity( message ).build();
    }


    private Response notFound( String message )
    {
        return Response.status( Response.Status.NOT_FOUND ).entity( message ).build();
    }

}

