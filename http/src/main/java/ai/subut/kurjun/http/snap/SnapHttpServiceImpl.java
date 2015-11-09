package ai.subut.kurjun.http.snap;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.utils.SnapUtils;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.security.Permission;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.security.service.AuthManager;


class SnapHttpServiceImpl implements SnapHttpService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( SnapHttpServiceImpl.class );

    private RepositoryFactory repositoryFactory;
    private AuthManager authManager;
    private KurjunContext context;

    @HeaderParam( KurjunConstants.HTTP_HEADER_FINGERPRINT )
    private String fingerprintHeader;

    @QueryParam( "fingerprint" )
    private String fingerprintParam;


    @Inject
    public SnapHttpServiceImpl( RepositoryFactory repositoryFactory,
                                AuthManager authManager,
                                @Assisted String context )
    {
        this.repositoryFactory = repositoryFactory;
        this.authManager = authManager;
        this.context = new KurjunContext( context );
    }


    @Override
    public Response getSnapInfo( String md5, String name, String version )
    {
        if ( checkAuthentication( Permission.GET_PACKAGE ) )
        {
            return Response.status( Status.FORBIDDEN ).entity( "No permission." ).build();
        }

        DefaultMetadata meta = new DefaultMetadata();
        meta.setMd5sum( decodeMd5Param( md5 ) );
        meta.setName( name );
        meta.setVersion( version );

        SerializableMetadata m = getRepository().getPackageInfo( meta );
        if ( m != null )
        {
            return Response.ok( m.serialize() ).build();
        }
        else
        {
            return Response.status( Status.NOT_FOUND ).entity( "Package not found." ).build();
        }
    }


    @Override
    public Response getSnapFile( String md5 )
    {
        if ( checkAuthentication( Permission.GET_PACKAGE ) )
        {
            return Response.status( Status.FORBIDDEN ).entity( "No permission." ).build();
        }

        DefaultMetadata m = new DefaultMetadata();
        m.setMd5sum( decodeMd5Param( md5 ) );

        LocalRepository repo = getRepository();
        SerializableMetadata meta = repo.getPackageInfo( m );
        if ( meta != null )
        {
            InputStream is = repo.getPackageStream( meta );
            if ( is != null )
            {
                String fileName = SnapUtils.makeFileName( meta );
                return Response.ok( is ).header( "Content-Disposition", "attachment; filename=" + fileName ).build();
            }
            return Response.serverError().entity( "Package file not found." ).build();
        }
        else
        {
            return Response.status( Status.NOT_FOUND ).entity( "Package not found." ).build();
        }
    }


    @Override
    public Response upload( Attachment attachment )
    {
        if ( checkAuthentication( Permission.ADD_PACKAGE ) )
        {
            return Response.status( Status.FORBIDDEN ).entity( "No permission." ).build();
        }

        // define file extension based on submitted file name
        String fileName = attachment.getContentDisposition().getParameter( "filename" );
        CompressionType compressionType = CompressionType.getCompressionType( fileName );

        try ( InputStream is = attachment.getObject( InputStream.class ) )
        {
            Metadata meta = getRepository().put( is, compressionType );
            URI location = new URI( null, null, "/info?md5=" + Hex.encodeHexString( meta.getMd5Sum() ), null );
            return Response.created( location ).build();
        }
        catch ( IOException | URISyntaxException ex )
        {
            LOGGER.error( "Failed to save package", ex );
            return Response.serverError().entity( "Failed to save package" ).build();
        }
    }


    @Override
    public Response deleteTemplates( String md5 )
    {
        if ( checkAuthentication( Permission.DEL_PACKAGE ) )
        {
            return Response.status( Status.FORBIDDEN ).entity( "No permission." ).build();
        }

        byte[] md5bin = decodeMd5Param( md5 );
        if ( md5bin != null )
        {
            try
            {
                getRepository().delete( md5bin );
                return Response.ok( "Package removed" ).build();
            }
            catch ( IOException ex )
            {
                String m = "Failed to delete package";
                LOGGER.error( m, ex );
                return Response.serverError().entity( m ).build();
            }
        }
        return Response.status( Status.BAD_REQUEST ).entity( "Invalid md5 checksum" ).build();
    }


    /**
     * TODO: move authentication check to some base class so that it is reusable
     *
     * @param permission
     * @return
     */
    private boolean checkAuthentication( Permission permission )
    {
        String f = Optional.ofNullable( fingerprintHeader ).orElse( fingerprintParam );
        return authManager.isAllowed( f, permission, context );
    }


    private LocalRepository getRepository()
    {
        return repositoryFactory.createLocalSnap( context );
    }


    private byte[] decodeMd5Param( String md5 )
    {
        try
        {
            return Hex.decodeHex( md5.toCharArray() );
        }
        catch ( DecoderException ex )
        {
            LOGGER.info( "Invalid md5 checksum", ex );
            return null;
        }
    }

}

