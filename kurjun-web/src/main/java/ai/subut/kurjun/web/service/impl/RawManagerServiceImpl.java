package ai.subut.kurjun.web.service.impl;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.identity.service.RelationManager;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.ObjectType;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.raw.RawData;
import ai.subut.kurjun.model.repository.ArtifactId;
import ai.subut.kurjun.model.repository.UnifiedRepository;
import ai.subut.kurjun.repo.LocalRawRepository;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.repo.service.RepositoryManager;
import ai.subut.kurjun.web.context.ArtifactContext;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ai.subut.kurjun.web.service.RawManagerService;
import ninja.Renderable;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.utils.ResponseStreams;


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
    RepositoryManager repositoryManager;


    @Inject
    public RawManagerServiceImpl( final RepositoryFactory repositoryFactory, final ArtifactContext artifactContext )
    {
        this.repositoryFactory = repositoryFactory;
        this.artifactContext = artifactContext;
    }


    private void _local()
    {
        this.localPublicRawRepository = this.repositoryFactory.createLocalRaw(
                new KurjunContext( DEFAULT_RAW_REPO_NAME, ObjectType.RawRepo.getId(), "system-owner"  ) );
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

        ArtifactId id = repositoryManager.constructArtifactId( repository, ObjectType.RawRepo.getId(), md5 );

        RawData rawData = ( RawData ) this.unifiedRepository.getPackageInfo( id );

        if ( rawData != null )
        {
            InputStream inputStream = this.unifiedRepository.getPackageStream( id );
            if ( inputStream != null )
            {
                return ( context, result ) -> {

                    result.addHeader( "Content-Disposition", "attachment;filename=" + rawData.getName() );
                    result.addHeader( "Content-Type", "application/octet-stream" );
                    result.addHeader( "Content-Length", String.valueOf( rawData.getSize() ) );

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

        ArtifactId id = repositoryManager.constructArtifactId( repository, ObjectType.RawRepo.getId(), md5 );

        try
        {
            String objectId = repository + "." + md5;

            //***** Check permissions (DELETE) *****************
            if ( checkRepoPermissions( userSession,repository , objectId, Permission.Delete ) )
            {
                relationManager.removeRelationsByTrustObject( objectId, ObjectType.Artifact.getId() );
                return localPublicRawRepository.delete( id );
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public SerializableMetadata getInfo( String repository, String md5, String search )
    {

        try
        {
            ArtifactId id = repositoryManager.constructArtifactId( repository, ObjectType.RawRepo.getId(), md5 );
            return unifiedRepository.getPackageInfo( id );
        }
        catch ( Exception ex )
        {
            return null;
        }
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
            relationManager.setObjectOwner( userSession.getUser(), repository, ObjectType.RawRepo.getId() );
            //**************************************

            //***** Check permissions (WRITE) *****************
            if ( checkRepoPermissions( userSession, repository, null, Permission.Write ) )
            {
                LocalRawRepository localRawRepository =
                        getLocalPublicRawRepository( userSession, new KurjunContext( repository ) );
                metadata =
                        localRawRepository.put( file, filename, repository, userSession.getUser().getKeyFingerprint() );

                //***** Build Relation ****************
                relationManager
                        .buildTrustRelation( userSession.getUser(), userSession.getUser(), metadata.getId().toString(),
                                ObjectType.Artifact.getId(), relationManager.buildPermissions( 4 ) );
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
        relationManager.setObjectOwner( userSession.getUser(), context.getName(), ObjectType.RawRepo.getId() );
        //**************************************

        return repositoryFactory.createLocalRaw( context );
    }


    @Override
    public List<SerializableMetadata> list( String repository, String search )
    {
        switch ( search )
        {
            //return local list
            case "local":
                return localPublicRawRepository.listPackages( repository, ObjectType.RawRepo.getId() );
            //return unified repo list
            case "all":
                return unifiedRepository.listPackages( repository, ObjectType.RawRepo.getId() );
            //return personal repository list
            default:
                return repositoryFactory.createLocalApt( new KurjunContext( repository ) ).listPackages();
        }
    }


    //*******************************************************************
    private boolean checkRepoPermissions( UserSession userSession, String repoId, String contentId, Permission perm )
    {
        return relationManager
                .checkObjectPermissions( userSession.getUser(), repoId, ObjectType.RawRepo.getId(), contentId,
                        ObjectType.Artifact.getId(), perm );
    }
    //*******************************************************************
}
