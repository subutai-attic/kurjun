package ai.subut.kurjun.web.service.impl;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.identity.service.RelationManager;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.raw.RawMetadata;
import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.RelationObjectType;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.UnifiedRepository;
import ai.subut.kurjun.repo.LocalRawRepository;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.web.context.ArtifactContext;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ai.subut.kurjun.web.service.RawManagerService;
import ninja.Renderable;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.utils.ResponseStreams;

import static com.google.common.base.Preconditions.checkNotNull;


@Singleton
public class RawManagerServiceImpl implements RawManagerService
{
    public static final String DEFAULT_RAW_REPO_NAME = "raw";

    private RepositoryFactory repositoryFactory;
    private LocalRawRepository localPublicRawRepository;
    private UnifiedRepository unifiedRepository;
    private ArtifactContext artifactContext;

    @Inject
    IdentityManagerService identityManagerService;

    @Inject
    RelationManager relationManager;


    @Inject
    public RawManagerServiceImpl( final RepositoryFactory repositoryFactory, final ArtifactContext artifactContext )
    {
        this.repositoryFactory = repositoryFactory;
        this.artifactContext = artifactContext;
    }


    private void _local()
    {
        this.localPublicRawRepository =
                this.repositoryFactory.createLocalRaw( new KurjunContext( DEFAULT_RAW_REPO_NAME ) );
    }


    private void _unified()
    {
        this.unifiedRepository = this.repositoryFactory.createUnifiedRepo();
        this.unifiedRepository.getRepositories().add( this.localPublicRawRepository );
        this.unifiedRepository.getRepositories().addAll( this.artifactContext.getRemoteRawRepositories() );
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


    @Override
    public Renderable getFile( final String name )
    {
        checkNotNull( name, "Name cannot be empty" );

        DefaultMetadata metadata = new DefaultMetadata();
        metadata.setName( name );

        RawMetadata meta = ( RawMetadata ) this.unifiedRepository.getPackageInfo( metadata );

        if ( meta != null )
        {
            InputStream inputStream = this.unifiedRepository.getPackageStream( meta );

            if ( inputStream != null )
            {
                return ( context, result ) -> {

                    result.addHeader( "Content-Disposition", "attachment;filename=" + meta.getName() );
                    result.addHeader( "Content-Type", "application/octet-stream" );
                    result.addHeader( "Content-Length", String.valueOf( meta.getSize() ) );

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
        }
        return null;
    }


    @Override
    public String md5()
    {
        return localPublicRawRepository.md5();
    }


    @Override
    public Renderable getFile( String repository, final String md5 )
    {

        DefaultMetadata defaultMetadata = new DefaultMetadata();

        defaultMetadata.setMd5sum( md5 );
        defaultMetadata.setFingerprint( repository );

        RawMetadata meta = ( RawMetadata ) this.unifiedRepository.getPackageInfo( defaultMetadata );

        if ( meta != null )
        {
            InputStream inputStream = this.unifiedRepository.getPackageStream( meta );
            if ( inputStream != null )
            {
                return ( context, result ) -> {

                    result.addHeader( "Content-Disposition", "attachment;filename=" + meta.getName() );
                    result.addHeader( "Content-Type", "application/octet-stream" );
                    result.addHeader( "Content-Length", String.valueOf( meta.getSize() ) );

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
        }

        return null;
    }


    @Override
    public Renderable getFile( final String md5, final boolean isKurjun )
    {
        return getFile( DEFAULT_RAW_REPO_NAME, md5 );
    }


    @Override
    public boolean delete( UserSession userSession, String repository, final String md5 )
    {
        DefaultMetadata defaultMetadata = new DefaultMetadata();
        defaultMetadata.setFingerprint( repository );
        defaultMetadata.setMd5sum( md5 );
        try
        {
            String objectId = defaultMetadata.getId().toString();

            //***** Check permissions (DELETE) *****************
            if ( checkRepoPermissions( userSession, "raw", objectId, Permission.Delete ) )
            {
                relationManager.removeRelationsByTrustObject( objectId, RelationObjectType.RepositoryContent.getId() );

                return localPublicRawRepository.delete( defaultMetadata.getId(), md5 );
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public SerializableMetadata getInfo( final String md5 )
    {
        DefaultMetadata metadata = new DefaultMetadata();
        metadata.setMd5sum( md5 );

        return unifiedRepository.getPackageInfo( metadata );
    }


    @Override
    public SerializableMetadata getInfo( final Metadata metadata )
    {

        return unifiedRepository.getPackageInfo( metadata );
    }


    @Override
    public Metadata put( UserSession userSession, final File file )
    {
        Metadata metadata = null;
        try
        {
            // *******CheckRepoOwner ***************
            relationManager.setObjectOwner( userSession.getUser(), "raw", RelationObjectType.RepositoryRaw.getId() );
            //**************************************

            //***** Check permissions (WRITE) *****************
            if ( checkRepoPermissions( userSession, "raw", null, Permission.Write ) )
            {
                metadata = localPublicRawRepository.put( file, CompressionType.NONE, DEFAULT_RAW_REPO_NAME );

                //***** Build Relation ****************
                relationManager
                        .buildTrustRelation( userSession.getUser(), userSession.getUser(), metadata.getId().toString(),
                                RelationObjectType.RepositoryContent.getId(), relationManager.buildPermissions( 4 ) );
                //*************************************
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return metadata;
    }


    @Override
    public Metadata put( UserSession userSession, final File file, final String repository )
    {
        Metadata metadata = null;
        try
        {
            // *******CheckRepoOwner ***************
            relationManager.setObjectOwner( userSession.getUser(), "raw", RelationObjectType.RepositoryRaw.getId() );
            //**************************************

            //***** Check permissions (WRITE) *****************
            if ( checkRepoPermissions( userSession, "raw", null, Permission.Write ) )
            {
                metadata =
                        localPublicRawRepository.put( new FileInputStream( file ), CompressionType.NONE, repository );

                //***** Build Relation ****************
                relationManager
                        .buildTrustRelation( userSession.getUser(), userSession.getUser(), metadata.getId().toString(),
                                RelationObjectType.RepositoryContent.getId(), relationManager.buildPermissions( 4 ) );
                //*************************************
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return metadata;
    }


    @Override
    public Metadata put( UserSession userSession, final File file, final String filename, final String repository )
    {

        if ( userSession.getUser().equals( identityManagerService.getPublicUser() ) )
        {
            return null;
        }

        Metadata metadata = null;
        try
        {
            // *******CheckRepoOwner ***************
            relationManager.setObjectOwner( userSession.getUser(), "raw", RelationObjectType.RepositoryRaw.getId() );
            //**************************************

            //***** Check permissions (WRITE) *****************
            if ( checkRepoPermissions( userSession, "raw", null, Permission.Write ) )
            {
                LocalRawRepository localRawRepository =
                        getLocalPublicRawRepository( userSession, new KurjunContext( repository ) );
                metadata = localRawRepository.put( file, filename, repository );

                //***** Build Relation ****************
                relationManager
                        .buildTrustRelation( userSession.getUser(), userSession.getUser(), metadata.getId().toString(),
                                RelationObjectType.RepositoryContent.getId(), relationManager.buildPermissions( 4 ) );
                //*************************************
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return metadata;
    }


    public LocalRawRepository getLocalPublicRawRepository( UserSession userSession, KurjunContext context )
    {
        // *******CheckRepoOwner ***************
        relationManager
                .setObjectOwner( userSession.getUser(), context.getName(), RelationObjectType.RepositoryRaw.getId() );
        //**************************************

        return repositoryFactory.createLocalRaw( context );
    }


    @Override
    public List<SerializableMetadata> list( String repository )
    {
        switch ( repository )
        {
            //return local list
            case "local":
                return localPublicRawRepository.listPackages();
            //return unified repo list
            case "all":
                return unifiedRepository.listPackages();
            //return personal repository list
            default:
                return repositoryFactory.createLocalApt( new KurjunContext( repository ) ).listPackages();
        }
    }


    //*******************************************************************
    private boolean checkRepoPermissions( UserSession userSession, String repoId, String contentId, Permission perm )
    {
        return relationManager
                .checkObjectPermissions( userSession.getUser(), repoId, RelationObjectType.RepositoryRaw.getId(),
                        contentId, RelationObjectType.RepositoryContent.getId(), perm );
    }
    //*******************************************************************
}
