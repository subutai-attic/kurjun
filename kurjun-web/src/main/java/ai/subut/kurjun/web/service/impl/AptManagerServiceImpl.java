package ai.subut.kurjun.web.service.impl;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Hex;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.RelationObjectType;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.repository.UnifiedRepository;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.repo.service.PackageFilenameParser;
import ai.subut.kurjun.repo.service.PackagesIndexBuilder;
import ai.subut.kurjun.repo.util.AptIndexBuilderFactory;
import ai.subut.kurjun.repo.util.PackagesProviderFactory;
import ai.subut.kurjun.repo.util.ReleaseIndexBuilder;
import ai.subut.kurjun.web.context.ArtifactContext;
import ai.subut.kurjun.web.service.AptManagerService;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ai.subut.kurjun.web.service.RelationManagerService;
import ai.subut.kurjun.web.utils.Utils;
import ninja.Renderable;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.utils.ResponseStreams;


@Singleton
public class AptManagerServiceImpl implements AptManagerService
{

    private static final Logger LOGGER = LoggerFactory.getLogger( AptManagerServiceImpl.class );

    private RepositoryFactory repositoryFactory;
    private AptIndexBuilderFactory aptIndexBuilderFactory;
    private PackagesProviderFactory packagesProviderFactory;
    private PackageFilenameParser packageFilenameParser;

    private ArtifactContext artifactContext;
    private LocalRepository localRepository;
    private UnifiedRepository unifiedRepository;

    private KurjunContext kurjunContext;

    public static final String REPO_NAME = "vapt";

    @Inject
    IdentityManagerService identityManagerService;
    @Inject
    RelationManagerService relationManagerService;


    @Inject
    public AptManagerServiceImpl( final RepositoryFactory repositoryFactory, final ArtifactContext artifactContext,
                                  final AptIndexBuilderFactory aptIndexBuilderFactory,
                                  final PackagesProviderFactory packagesProviderFactory,
                                  final PackageFilenameParser packageFilenameParser )
    {
        this.repositoryFactory = repositoryFactory;
        this.artifactContext = artifactContext;
        this.aptIndexBuilderFactory = aptIndexBuilderFactory;
        this.packagesProviderFactory = packagesProviderFactory;
        this.packageFilenameParser = packageFilenameParser;

    }



    @Start( order = 90 )
    public void startService()
    {
        _local();

        _unified();
    }


    @Dispose( order = 90 )
    public void stopService()
    {

    }


    //init local repos
    private void _local()
    {
        this.kurjunContext = new KurjunContext( REPO_NAME );
        this.localRepository = repositoryFactory.createLocalApt( kurjunContext );
    }


    //init remote repos
    private void _unified()
    {
        this.unifiedRepository = repositoryFactory.createUnifiedRepo();
        this.unifiedRepository.getRepositories().add( this.localRepository );
        this.unifiedRepository.getRepositories().addAll( artifactContext.getRemoteAptRepositories() );
    }


    @Override
    public String md5()
    {
        return Utils.MD5.toString( localRepository.md5() );
    }


    @Override
    public String getRelease( final String release, final String component, final String arch )
    {
        Optional<ReleaseFile> releaseFile = unifiedRepository.getDistributions().stream().findFirst();

        if ( releaseFile.isPresent() )
        {
            ReleaseIndexBuilder releaseIndexBuilder =
                    aptIndexBuilderFactory.createReleaseIndexBuilder( unifiedRepository, kurjunContext );
            return releaseIndexBuilder.build( releaseFile.get(), unifiedRepository.isKurjun() );
        }

        return null;
    }


    public Renderable getPackagesIndex( final String release, final String component, final String arch,
                                        final String packagesIndex ) throws IllegalArgumentException
    {
        InputStream inputStream = getPackagesIndexStream( release, component, arch, packagesIndex );

        if ( inputStream != null )
        {
            return ( context, result ) -> {

                ResponseStreams responseStreams = context.finalizeHeaders( result );
                try ( OutputStream outputStream = responseStreams.getOutputStream() )
                {
                    ByteStreams.copy( inputStream, outputStream );
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
            };
        }
        return null;
    }


    public Renderable getPackageByFilename( final String filename ) throws IllegalArgumentException
    {
        InputStream inputStream = getPackageByFilenameStream( filename );

        if ( inputStream != null )
        {
            return ( context, result ) -> {

                ResponseStreams responseStreams = context.finalizeHeaders( result );
                try ( OutputStream outputStream = responseStreams.getOutputStream() )
                {
                    ByteStreams.copy( inputStream, outputStream );
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
            };
        }
        return null;
    }


    @Override
    public String getPackageInfo( final byte[] md5, final String name, final String version )
    {
        if ( md5 == null && name == null && version == null )
        {
            return null;
        }

        DefaultMetadata m = new DefaultMetadata();
        m.setMd5sum( md5 );
        m.setName( name );
        m.setVersion( version );

        SerializableMetadata meta = unifiedRepository.getPackageInfo( m );
        if ( meta != null )
        {
            return meta.serialize();
        }
        return null;
    }


    @Override
    public Renderable getPackage( final byte[] md5 )
    {
        DefaultMetadata m = new DefaultMetadata();
        m.setMd5sum( md5 );

        DefaultPackageMetadata md = ( DefaultPackageMetadata ) unifiedRepository.getPackageInfo( m );

        InputStream inputStream = getPackageStream( md5 );

        if ( inputStream != null )
        {
            return ( context, result ) -> {
                result.addHeader( "Content-Disposition",
                        "attachment;filename=" + md.getName() + "_" + md.getVersion() + "_" + md.getArchitecture()
                                + ".deb" );
                result.addHeader( "Content-Type", "application/octet-stream" );
                //result.addHeader( "Content-Length", String.valueOf( md.getInstalledSize() ) );

                ResponseStreams responseStreams = context.finalizeHeaders( result );
                try ( OutputStream outputStream = responseStreams.getOutputStream() )
                {
                    ByteStreams.copy( inputStream, outputStream );
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
            };
        }

        return null;
    }


    @Override
    public URI upload(UserSession userSession,  final InputStream is )
    {

        if ( userSession.getUser().equals( identityManagerService.getPublicUser() ) )
        {
            return null;
        }

        try
        {
            // *******CheckRepoOwner ***************
            relationManagerService
                    .checkRelationOwner( userSession, REPO_NAME, RelationObjectType.RepositoryApt.getId() );
            //**************************************

            //***** Check permissions (WRITE) *****************
            if ( checkRepoPermissions(userSession, REPO_NAME, null, Permission.Write ) )
            {
                Metadata meta = localRepository.put( is );
                if ( meta != null )
                {
                    //***** Build Relation ****************
                    relationManagerService
                            .buildTrustRelation( userSession.getUser(), userSession.getUser(), meta.getId().toString(),
                                    RelationObjectType.RepositoryContent.getId(),
                                    relationManagerService.buildPermissions( 4 ) );
                    //*************************************

                    return new URI( null, null, "/info", "md5=" + Hex.encodeHexString( meta.getMd5Sum() ), null );
                }
            }
        }
        catch ( IOException | URISyntaxException e )
        {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<SerializableMetadata> list( String repository )
    {
        List<SerializableMetadata> list;

        switch ( repository )
        {
            //return local list
            case "public":
                list = localRepository.listPackages();
                break;
            //return unified repo list
            case "all":
                list = unifiedRepository.listPackages();
                break;
            //return personal repository list
            default:
                list = repositoryFactory.createLocalTemplate( new KurjunContext( repository ) ).listPackages();
                break;
        }

        return list.stream().map( pkg -> ( DefaultPackageMetadata ) pkg ).collect( Collectors.toList() );
    }


    @Override
    public boolean delete(UserSession userSession, final byte[] md5 )
    {
        try
        {
            String id = Hex.encodeHexString( md5 );

            if ( checkRepoPermissions( userSession, REPO_NAME, id, Permission.Delete ) )
            {
                // remove relation
                relationManagerService.removeRelationsByTrustObject( id, RelationObjectType.RepositoryContent.getId() );

                return localRepository.delete( md5 );
            }
        }
        catch ( IOException ex )
        {
            ex.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean isCompressionTypeSupported( final String packagesIndex )
    {
        return CompressionType.getCompressionType( packagesIndex ) != CompressionType.NONE;
    }


    @Override
    public String getSerializedPackageInfo( final String filename ) throws IllegalArgumentException
    {
        SerializableMetadata meta = getPackageInfoByFilename( filename );
        return ( meta != null ) ? meta.serialize() : null;
    }


    @Override
    public String getSerializedPackageInfo( final byte[] md5 ) throws IllegalArgumentException
    {
        DefaultMetadata m = new DefaultMetadata();
        m.setMd5sum( md5 );
        SerializableMetadata meta = unifiedRepository.getPackageInfo( m );

        return ( meta != null ) ? meta.serialize() : null;
    }


    private SerializableMetadata getPackageInfoByFilename( String filename ) throws IllegalArgumentException
    {
        String path = "/pool/" + filename;
        String packageName = packageFilenameParser.getPackageFromFilename( path );
        String version = packageFilenameParser.getVersionFromFilename( path );

        if ( packageName == null || version == null )
        {
            throw new IllegalArgumentException( "Invalid pool path" );
        }

        DefaultMetadata m = new DefaultMetadata();
        m.setName( packageName );
        m.setVersion( version );

        return unifiedRepository.getPackageInfo( m );
    }


    private InputStream getPackagesIndexStream( String release, String component, String arch, String packagesIndex )
            throws IllegalArgumentException
    {
        Optional<ReleaseFile> distr =
                unifiedRepository.getDistributions().stream().filter( r -> r.getCodename().equals( release ) )
                                 .findFirst();
        if ( !distr.isPresent() )
        {
            throw new IllegalArgumentException( "Release not found." );
        }
        if ( distr.get().getComponent( component ) == null )
        {
            throw new IllegalArgumentException( "Component not found." );
        }

        // arch string is like "binary-amd64"
        Architecture architecture = Architecture.getByValue( arch.substring( arch.indexOf( "-" ) + 1 ) );
        if ( architecture == null )
        {
            throw new IllegalArgumentException( "Architecture not supported." );
        }

        CompressionType compressionType = CompressionType.getCompressionType( packagesIndex );


        PackagesIndexBuilder packagesIndexBuilder = aptIndexBuilderFactory.createPackagesIndexBuilder( kurjunContext );

        try ( ByteArrayOutputStream os = new ByteArrayOutputStream() )
        {
            packagesIndexBuilder
                    .buildIndex( packagesProviderFactory.create( unifiedRepository, component, architecture ), os,
                            compressionType );

            return new ByteArrayInputStream( os.toByteArray() );
        }
        catch ( IOException ex )
        {
            ex.printStackTrace();
        }

        throw new IllegalArgumentException( "Failed to generate packages index." );
    }


    private InputStream getPackageByFilenameStream( String filename ) throws IllegalArgumentException
    {
        SerializableMetadata meta = getPackageInfoByFilename( filename );
        return ( meta != null ) ? unifiedRepository.getPackageStream( meta ) : null;
    }


    private InputStream getPackageStream( byte[] md5 )
    {
        DefaultMetadata defaultMetadata = new DefaultMetadata();
        defaultMetadata.setMd5sum( md5 );
        unifiedRepository.getPackageInfo( defaultMetadata );
        return unifiedRepository.getPackageStream( defaultMetadata );
    }



    //*******************************************************************
    private boolean checkRepoPermissions(UserSession userSession, String repoId, String contentId, Permission perm )
    {
        return relationManagerService
                .checkRepoPermissions( userSession, repoId, RelationObjectType.RepositoryApt.getId(), contentId,
                        RelationObjectType.RepositoryContent.getId(), perm );
    }
    //*******************************************************************
}
