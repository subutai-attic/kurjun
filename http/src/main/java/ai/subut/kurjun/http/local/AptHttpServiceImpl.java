package ai.subut.kurjun.http.local;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Optional;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.http.HttpServiceBase;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.security.Permission;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.repo.service.PackageFilenameBuilder;
import ai.subut.kurjun.repo.service.PackageFilenameParser;
import ai.subut.kurjun.repo.service.PackagesIndexBuilder;
import ai.subut.kurjun.repo.util.AptIndexBuilderFactory;
import ai.subut.kurjun.repo.util.ReleaseIndexBuilder;
import ai.subut.kurjun.security.service.AuthManager;


public class AptHttpServiceImpl extends HttpServiceBase implements AptHttpService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AptHttpServiceImpl.class );

    private RepositoryFactory repositoryFactory;
    private AuthManager authManager;
    private AptIndexBuilderFactory indexBuilderFactory;
    private PackageFilenameParser filenameParser;
    private PackageFilenameBuilder filenameBuilder;
    private Gson gson;

    private KurjunContext context;


    @Inject
    public AptHttpServiceImpl( RepositoryFactory repositoryFactory,
                               AuthManager authManager,
                               AptIndexBuilderFactory indexBuilderFactory,
                               PackageFilenameParser filenameParser,
                               PackageFilenameBuilder filenameBuilder,
                               Gson gson,
                               @Assisted String context )
    {
        this.repositoryFactory = repositoryFactory;
        this.authManager = authManager;
        this.indexBuilderFactory = indexBuilderFactory;
        this.filenameParser = filenameParser;
        this.filenameBuilder = filenameBuilder;
        this.gson = gson;
        this.context = new KurjunContext( context );
    }


    @Override
    public Response getRelease( String release, String component, String arch )
    {
        LocalRepository repo = getRepository();
        Optional<ReleaseFile> rel = repo.getDistributions().stream()
                .filter( r -> r.getCodename().equals( release ) ).findFirst();

        if ( rel.isPresent() )
        {
            ReleaseIndexBuilder rib = indexBuilderFactory.createReleaseIndexBuilder( context );
            String releaseIndex = rib.build( rel.get(), repo.isKurjun() );
            return Response.ok( releaseIndex ).build();
        }
        return notFoundResponse( "Release not found." );
    }


    @Override
    public Response getPackagesIndex( String release, String component, String arch, String packagesIndex )
    {
        LocalRepository repo = getRepository();
        Optional<ReleaseFile> distr = repo.getDistributions().stream()
                .filter( r -> r.getCodename().equals( release ) ).findFirst();
        if ( !distr.isPresent() )
        {
            return notFoundResponse( "Release not found." );
        }
        if ( distr.get().getComponent( component ) == null )
        {
            return notFoundResponse( "Component not found." );
        }

        // arch string is like "binary-amd64"
        Architecture architecture = Architecture.getByValue( arch.substring( arch.indexOf( "-" ) + 1 ) );
        if ( architecture == null )
        {
            return notFoundResponse( "Architecture not supported." );
        }

        Response.ResponseBuilder rb = Response.ok();

        // make archived package indices downloadable by specifying content disposition header
        CompressionType compressionType = CompressionType.getCompressionType( packagesIndex );
        if ( compressionType != CompressionType.NONE )
        {
            rb.header( "Content-Disposition", " attachment; filename=" + packagesIndex );
        }

        PackagesIndexBuilder packagesIndexBuilder = indexBuilderFactory.createPackagesIndexBuilder( context );
        try ( ByteArrayOutputStream os = new ByteArrayOutputStream() )
        {
            packagesIndexBuilder.buildIndex( component, architecture, os, compressionType );
            return rb.entity( new ByteArrayInputStream( os.toByteArray() ) ).build();
        }
        catch ( IOException ex )
        {
            LOGGER.error( "Failed to build packages index", ex );
        }

        return Response.serverError().entity( "Failed to generate packages index." ).build();
    }


    @Override
    public Response getPackageByFilename( String filename )
    {
        if ( checkAuthentication( Permission.GET_PACKAGE ) )
        {
            return forbiddenResponse();
        }

        String path = "/pool/" + filename;
        String packageName = filenameParser.getPackageFromFilename( path );
        String version = filenameParser.getVersionFromFilename( path );
        if ( packageName == null || version == null )
        {
            return Response.status( Status.BAD_REQUEST ).entity( "Invalid pool path" ).build();
        }

        DefaultMetadata m = new DefaultMetadata();
        m.setName( packageName );
        m.setVersion( version );

        LocalRepository repo = getRepository();
        SerializableMetadata meta = repo.getPackageInfo( m );
        if ( meta != null )
        {
            InputStream is = repo.getPackageStream( meta );
            if ( is != null )
            {
                DefaultPackageMetadata pm = gson.fromJson( meta.serialize(), DefaultPackageMetadata.class );
                return Response.ok( is )
                        .header( "Content-Disposition",
                                 "attachment; filename=" + filenameBuilder.makePackageFilename( pm ) )
                        .build();
            }
        }
        return packageNotFoundResponse();
    }


    @Override
    public Response upload( Attachment attachment )
    {
        if ( checkAuthentication( Permission.ADD_PACKAGE ) )
        {
            return forbiddenResponse();
        }

        File temp = null;
        try
        {
            temp = Files.createTempFile( "deb-upload", null ).toFile();
            attachment.transferTo( temp );
            try ( InputStream is = new FileInputStream( temp ) )
            {
                Metadata meta = getRepository().put( is );
                URI location = new URI( null, null, "/info", "md5=" + Hex.encodeHexString( meta.getMd5Sum() ), null );
                return Response.created( location ).build();
            }

        }
        catch ( IOException | URISyntaxException ex )
        {
            LOGGER.error( "Failed to upload", ex );
        }
        finally
        {
            FileUtils.deleteQuietly( temp );
        }
        return Response.serverError().entity( "Failed to upload package." ).build();
    }


    @Override
    public Response getPackageInfo( String md5, String name, String version )
    {
        if ( checkAuthentication( Permission.GET_PACKAGE ) )
        {
            return forbiddenResponse();
        }

        DefaultMetadata m = new DefaultMetadata();
        m.setMd5sum( decodeMd5Param( md5 ) );
        m.setName( name );
        m.setVersion( version );

        SerializableMetadata meta = getRepository().getPackageInfo( m );
        if ( meta != null )
        {
            return Response.ok( meta.serialize(), MediaType.APPLICATION_JSON ).build();
        }
        return packageNotFoundResponse();
    }


    @Override
    public Response getPackage( String md5 )
    {
        if ( checkAuthentication( Permission.GET_PACKAGE ) )
        {
            return forbiddenResponse();
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
                DefaultPackageMetadata pm = gson.fromJson( meta.serialize(), DefaultPackageMetadata.class );
                String filename = filenameBuilder.makePackageFilename( pm );
                return Response.ok( is ).header( "Content-Disposition", "attachment; filename=" + filename ).build();
            }
            return Response.serverError().entity( "Package file not found" ).build();
        }
        return packageNotFoundResponse();
    }


    @Override
    protected Logger getLogger()
    {
        return LOGGER;
    }


    @Override
    protected KurjunContext getContext()
    {
        return context;
    }


    @Override
    protected AuthManager getAuthManager()
    {
        return authManager;
    }


    private LocalRepository getRepository()
    {
        return repositoryFactory.createLocalApt( context );
    }

}

