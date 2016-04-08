package ai.subut.kurjun.web.service.impl;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.identity.service.RelationManager;
import ai.subut.kurjun.model.identity.ObjectType;
import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.apt.AptData;
import ai.subut.kurjun.model.repository.ArtifactId;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.repository.UnifiedRepository;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.repo.service.PackageFilenameParser;
import ai.subut.kurjun.repo.service.PackagesIndexBuilder;
import ai.subut.kurjun.repo.service.RepositoryManager;
import ai.subut.kurjun.repo.util.AptIndexBuilderFactory;
import ai.subut.kurjun.repo.util.PackagesProviderFactory;
import ai.subut.kurjun.repo.util.ReleaseIndexBuilder;
import ai.subut.kurjun.web.context.ArtifactContext;
import ai.subut.kurjun.web.service.AptManagerService;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ai.subut.kurjun.web.service.RepositoryService;
import ninja.Renderable;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.utils.ResponseStreams;


@Singleton
public class AptManagerServiceImpl implements AptManagerService
{

    private static final Logger LOGGER = LoggerFactory.getLogger( AptManagerServiceImpl.class );

    public static final String REPO_NAME = "apt";

    private RepositoryFactory repositoryFactory;
    private AptIndexBuilderFactory aptIndexBuilderFactory;
    private PackagesProviderFactory packagesProviderFactory;
    private PackageFilenameParser packageFilenameParser;

    private ArtifactContext artifactContext;
    private LocalRepository localRepository;
    private UnifiedRepository unifiedRepository;

    private KurjunContext kurjunContext;

    @Inject
    IdentityManagerService identityManagerService;
    @Inject
    RelationManager relationManager;

    @Inject
    RepositoryManager repositoryManager;

    @Inject
    RepositoryService repositoryService;


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
        this.kurjunContext = new KurjunContext( "subutai", ObjectType.AptRepo.getId(), "system-owner" );
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
        return localRepository.md5();
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
    public String getPackageInfo( UserSession userSession, String repository, final String md5, final String name,
                                  final String version )
    {
        if ( md5 == null && name == null && version == null )
        {
            return null;
        }

        ArtifactId id = repositoryManager.constructArtifactId( repository, ObjectType.AptRepo.getId(), md5 );
        id.setVersion( version );
        id.setArtifactName( name );

        SerializableMetadata meta = unifiedRepository.getPackageInfo( id );
        if ( meta != null )
        {
            return meta.serialize();
        }
        return null;
    }


    @Override
    public Renderable getPackage( UserSession userSession, final String md5 )
    {
        ArtifactId id = repositoryManager.constructArtifactId( "", ObjectType.AptRepo.getId(), md5 );

        AptData md = ( AptData ) unifiedRepository.getPackageInfo( id );

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
    public URI upload( UserSession userSession, String repository, final InputStream is )
    {

        if ( identityManagerService.isPublicUser( userSession.getUser() ) )
        {
            return null;
        }

        try
        {
            if ( Strings.isNullOrEmpty( repository ) )
            {
                repository = userSession.getUser().getUserName();
            }


            // *******CheckRepoOwner ***************
            relationManager.setObjectOwner( userSession.getUser(), repository, ObjectType.AptRepo.getId() );
            //**************************************

            //***** Check permissions (WRITE) *****************
            if ( checkRepoPermissions( userSession, repository, null, Permission.Write ) )
            {
                Metadata meta = localRepository
                        .put( is, CompressionType.NONE, repository, userSession.getUser().getKeyFingerprint() );
                if ( meta != null )
                {
                    //***** Build Relation ****************
                    relationManager
                            .buildTrustRelation( userSession.getUser(), userSession.getUser(), meta.getId().toString(),
                                    ObjectType.Artifact.getId(), relationManager.buildPermissions( 4 ) );
                    //*************************************

                    return new URI( null, null, "/info", "md5=" + meta.getMd5Sum(), null );
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
    public List<SerializableMetadata> list( UserSession userSession, String repository, String node )
    {

        List<SerializableMetadata> results = null;

        node = StringUtils.isBlank( node ) ? "local" : node;
        switch ( node )
        {
            //get local list
            case "local":
                //add local public artifacts
                results = localRepository.listPackages();
                break;

            default: // "all"
                //get unified repo list
                results = unifiedRepository.listPackages();
                break;
        }
        //public user, return results
        if ( identityManagerService.isPublicUser( userSession.getUser() ) )
        {
            return results == null? new ArrayList<>() : results;
        }

        //if repository is blank
        repository = StringUtils.isBlank( repository ) ? userSession.getUser().getUserName() : repository;


        //get personal repository list
        LocalRepository localUserRepo =
                repositoryFactory.createLocalApt( new KurjunContext( userSession.getUser().getUserName() ) );

        //user trying to get other repository that was shared with him
        //TODO:put security check here if user has permission for this repo
        if ( !repository.equalsIgnoreCase( userSession.getUser().getUserName() ) )
        {
            //create repo instance based on repository name
            LocalRepository privateSharedRepository =
                    repositoryFactory.createLocalApt( new KurjunContext( repository ) );

            //TODO:object level security check required?
            results.addAll( privateSharedRepository.listPackages() );
        }

        results.addAll( localUserRepo.listPackages() );

        return results == null? new ArrayList<>() : results;
    }


    @Override
    public boolean delete( UserSession userSession, String repository, final String md5 )
    {

        if ( identityManagerService.isPublicUser( userSession.getUser() ) )
        {
            return false;
        }


        try
        {
            ArtifactId artId = repositoryManager.constructArtifactId( repository, ObjectType.AptRepo.getId(), md5 );

            String uniqId = repository + "." + md5;

            if ( checkRepoPermissions( userSession, repository, uniqId, Permission.Delete ) )
            {
                // remove relation
                relationManager.removeRelationsByTrustObject( uniqId, ObjectType.Artifact.getId() );

                //return localRepository.delete( md5 );
                return localRepository.delete( artId );
            }
        }
        catch ( IOException ex )
        {
            //ex.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean isCompressionTypeSupported( final String packagesIndex )
    {
        return CompressionType.getCompressionType( packagesIndex ) != CompressionType.NONE;
    }


    @Override
    public String getSerializedPackageInfoByFilename( final String filename ) throws IllegalArgumentException
    {
        SerializableMetadata meta = getPackageInfoByFilename( filename );
        return ( meta != null ) ? meta.serialize() : null;
    }


    @Override
    public String getSerializedPackageInfoByMd5( final String md5 ) throws IllegalArgumentException
    {
        ArtifactId artId = repositoryManager.constructArtifactId( "", ObjectType.AptRepo.getId(), md5 );

        SerializableMetadata meta = unifiedRepository.getPackageInfo( artId );

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

        ArtifactId artId = repositoryManager.constructArtifactId( "", ObjectType.AptRepo.getId(), "" );
        artId.setArtifactName( packageName );
        artId.setVersion( version );

        return unifiedRepository.getPackageInfo( artId );
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

        ArtifactId artId = repositoryManager.constructArtifactId( "", ObjectType.AptRepo.getId(), meta.getMd5Sum() );

        return ( meta != null ) ? unifiedRepository.getPackageStream( artId ) : null;
    }


    private InputStream getPackageStream( String md5 )
    {
        ArtifactId artId = repositoryManager.constructArtifactId( "", ObjectType.AptRepo.getId(), md5 );
        return unifiedRepository.getPackageStream( artId );
    }


    //*******************************************************************
    private boolean checkRepoPermissions( UserSession userSession, String repoId, String contentId, Permission perm )
    {
        return relationManager
                .checkObjectPermissions( userSession.getUser(), repoId, ObjectType.AptRepo.getId(), contentId,
                        ObjectType.Artifact.getId(), perm );
    }
    //*******************************************************************

    @Override
    public List<String> getRepoList()
    {
        List<RepositoryData> repoList = repositoryService.getRepositoryList();
        List<String> repoNamesList = new ArrayList<>();
        repoList.forEach( r -> {
            if (r.getType() == ObjectType.AptRepo.getId())
                repoNamesList.add( r.getContext() );
        } );

        return repoNamesList;
    }
}
