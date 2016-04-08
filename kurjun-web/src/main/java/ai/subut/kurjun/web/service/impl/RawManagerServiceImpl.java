package ai.subut.kurjun.web.service.impl;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
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
import ai.subut.kurjun.model.metadata.RepositoryData;
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
import ai.subut.kurjun.web.service.RepositoryService;
import ninja.Renderable;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.utils.ResponseStreams;


@Singleton
public class RawManagerServiceImpl implements RawManagerService
{
    public static final String DEFAULT_RAW_REPO_NAME = "raw";

    private static final Logger LOGGER = LoggerFactory.getLogger( RawManagerServiceImpl.class );

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
    RepositoryService repositoryService;


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

        if ( identityManagerService.isPublicUser( userSession.getUser() ) )
        {
            return false;
        }

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
    public Metadata put( UserSession userSession, final File file, final String filename, String repository )
    {

        if ( identityManagerService.isPublicUser( userSession.getUser() ) )
        {
            return null;
        }

        if( Strings.isNullOrEmpty( repository ))
        {
            repository = userSession.getUser().getUserName();
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

                String uniqId = repository + "." + metadata.getMd5Sum();

                //***** Build Relation ****************
                relationManager
                        .buildTrustRelation( userSession.getUser(), userSession.getUser(), uniqId,
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
    public List<SerializableMetadata> list( UserSession userSession, String repository, String search )
    {
        List<SerializableMetadata> result;
        if( Strings.isNullOrEmpty( repository ))
        {
            repository = userSession.getUser().getUserName();
        }

        switch ( search )
        {
            //return local list
            case "local":
                result = localPublicRawRepository.listPackages( repository, ObjectType.RawRepo.getId() );
                break;
            //return unified repo list
            case "all":
                result = unifiedRepository.listPackages( repository, ObjectType.RawRepo.getId() );
                break;
            //return personal repository list
            default:
                result = repositoryFactory.createLocalApt( new KurjunContext( repository ) ).listPackages();
        }
/*
        //public user, return results
        if ( identityManagerService.isPublicUser( userSession.getUser() ) )
        {
            return results;
        }

        //if repository is blank
        repository = StringUtils.isBlank( repository ) ? userSession.getUser().getUserName() : repository;


        //get personal repository list
        LocalRepository localUserRepo =
                repositoryFactory.createLocalRaw( new KurjunContext( userSession.getUser().getUserName() ) );

        //user trying to get other repository that was shared with him
        //TODO:put security check here if user has permission for this repo
        if ( !repository.equalsIgnoreCase( userSession.getUser().getUserName() ) )
        {
            //create repo instance based on repository name
            LocalRepository privateSharedRepository =
                    repositoryFactory.createLocalRaw( new KurjunContext( repository ) );

            //TODO:object level security check required?
            results.addAll( privateSharedRepository.listPackages() );
        }

        results.addAll( localUserRepo.listPackages() );
*/

        return result == null? new ArrayList<>(  ) : result;
    }


    //*******************************************************************
    private boolean checkRepoPermissions( UserSession userSession, String repoId, String contentId, Permission perm )
    {
        return relationManager
                .checkObjectPermissions( userSession.getUser(), repoId, ObjectType.RawRepo.getId(), contentId,
                        ObjectType.Artifact.getId(), perm );
    }
    //*******************************************************************

    @Override
    public List<String> getRepoList()
    {
        return repositoryService.getRepositoryContextList( ObjectType.RawRepo.getId());
    }
}
