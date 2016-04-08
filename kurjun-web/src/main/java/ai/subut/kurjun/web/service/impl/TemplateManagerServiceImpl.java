package ai.subut.kurjun.web.service.impl;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.ErrorCode;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.identity.service.RelationManager;
import ai.subut.kurjun.model.identity.ObjectType;
import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.model.metadata.template.TemplateData;
import ai.subut.kurjun.model.repository.ArtifactId;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.repository.UnifiedRepository;
import ai.subut.kurjun.repo.LocalTemplateRepository;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.repo.service.RepositoryManager;
import ai.subut.kurjun.web.context.ArtifactContext;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ai.subut.kurjun.web.service.TemplateManagerService;
import ninja.Context;
import ninja.Renderable;
import ninja.Result;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.utils.ResponseStreams;


@Singleton
public class TemplateManagerServiceImpl implements TemplateManagerService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( TemplateManagerServiceImpl.class );

    //------------------------------
    @Inject
    IdentityManagerService identityManagerService;

    @Inject
    RepositoryManager repositoryManager;

    @Inject
    RelationManager relationManager;

    @Inject
    RepositoryServiceImpl repositoryService;

    //------------------------------

    private RepositoryFactory repositoryFactory;
    private ArtifactContext artifactContext;
    private LocalTemplateRepository localPublicTemplateRepository;
    private UnifiedRepository unifiedTemplateRepository;


    @Inject
    public TemplateManagerServiceImpl( final RepositoryFactory repositoryFactory,
                                       final ArtifactContext artifactContext )
    {
        this.repositoryFactory = repositoryFactory;
        this.artifactContext = artifactContext;
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


    //init local repo
    private void _local()
    {
        this.localPublicTemplateRepository = ( LocalTemplateRepository ) repositoryFactory
                .createLocalTemplate( new KurjunContext( "public", ObjectType.TemplateRepo.getId(), "system-owner" ) );
    }


    private void _unified()
    {
        this.unifiedTemplateRepository = repositoryFactory.createUnifiedRepo();

        if ( localPublicTemplateRepository != null )
        {
            this.unifiedTemplateRepository.getRepositories().add( localPublicTemplateRepository );
        }

        this.unifiedTemplateRepository.getRepositories().addAll( artifactContext.getRemoteTemplateRepositories() );
    }


    @Override
    public List<SerializableMetadata> list( UserSession userSession, String repository, String node,
                                            final boolean isKurjunClient ) throws IOException
    {
        List<SerializableMetadata> results = null;
        node = StringUtils.isBlank( node ) ? "local" : node;
        switch ( node )
        {
            //get local list
            case "local":
                //add local public artifacts
                results = localPublicTemplateRepository.listPackages();
                break;

            default: // "all"
                //get unified repo list
                results = unifiedTemplateRepository.listPackages();
                break;
        }
        //public user, return results
        if ( identityManagerService.isPublicUser( userSession.getUser() ) )
        {
            return results;
        }

        //if repository is blank
        repository = StringUtils.isBlank( repository ) ? userSession.getUser().getUserName() : repository;


        //get personal repository list
        LocalRepository localUserRepo =
                repositoryFactory.createLocalTemplate( new KurjunContext( userSession.getUser().getUserName() ) );

        //user trying to get other repository that was shared with him
        //TODO:put security check here if user has permission for this repo
        if ( !repository.equalsIgnoreCase( userSession.getUser().getUserName() ) )
        {
            //create repo instance based on repository name
            LocalRepository privateSharedRepository =
                    repositoryFactory.createLocalTemplate( new KurjunContext( repository ) );
            //TODO:object level security check required?
            results.addAll( privateSharedRepository.listPackages() );
        }


        results.addAll( localUserRepo.listPackages() );


        return results;
    }


    @Override
    public String upload( UserSession userSession, String repository, final InputStream inputStream ) throws IOException
    {

        if ( identityManagerService.isPublicUser( userSession.getUser() ) )
        {
            return null;
        }

        if ( Strings.isNullOrEmpty( repository ) )
        {
            repository = userSession.getUser().getUserName();
        }


        // *******CheckRepoOwner ***************
        relationManager.setObjectOwner( userSession.getUser(), repository, ObjectType.TemplateRepo.getId() );
        //**************************************

        //***** Check permissions (WRITE) *****************
        if ( checkRepoPermissions( userSession, repository, null, Permission.Write ) )
        {
            SubutaiTemplateMetadata metadata = ( SubutaiTemplateMetadata ) getRepo( repository )
                    .put( inputStream, CompressionType.GZIP, repository, userSession.getUser().getKeyFingerprint() );

            if ( metadata != null )
            {
                if ( metadata.getMd5Sum() != null )
                {

                    String templateId = repository + "." + metadata.getMd5Sum();

                    //***** Build Relation ****************
                    relationManager.buildTrustRelation( userSession.getUser(), userSession.getUser(), templateId,
                            ObjectType.Artifact.getId(), relationManager.buildPermissions( 4 ) );
                    //*************************************

                    return templateId;
                }
            }

            return null;
        }
        else
        {
            return null;
        }
    }


    @Override
    public int delete( UserSession userSession, String repository, String md5 ) throws IOException
    {
        //************ CheckPermissions ************************************
        ArtifactId id = repositoryManager.constructArtifactId( repository, ObjectType.TemplateRepo.getId(), md5 );
        String atifactId = repository + "." + md5;
        String fprint = userSession.getUser().getKeyFingerprint();


        if ( checkRepoPermissions( userSession, fprint, atifactId, Permission.Delete ) )
        {
            LocalTemplateRepository _repository = ( LocalTemplateRepository ) getRepo( repository );

            // remove Relation
            relationManager.removeRelationsByTrustObject( atifactId, ObjectType.Artifact.getId() );

            boolean success = _repository.delete( id );

            if ( success )
            {
                return ErrorCode.Success.getId();
            }

            return ErrorCode.ObjectNotFound.getId();
        }
        else
        {
            return ErrorCode.AccessPermissionError.getId();
        }
    }


    @Override
    public LocalRepository createUserRepository( final KurjunContext userName )
    {
        return repositoryFactory.createLocalTemplate( userName );
    }


    @Override
    public Renderable renderableTemplate( UserSession userSession, final String repository, String md5,
                                          final boolean isKurjunClient ) throws IOException
    {
        boolean allowed = true;
        //************ CheckPermissions ************************************
        //check if only not public
        if ( !repository.equalsIgnoreCase( "public" ) )
        {
            allowed = checkRepoPermissions( userSession, repository, repository + "." + md5, Permission.Read );
        }

        if ( allowed )
        {
            ArtifactId id = repositoryManager.constructArtifactId( repository, ObjectType.TemplateRepo.getId(), md5 );


            TemplateData templateData = ( TemplateData ) unifiedTemplateRepository.getPackageInfo( id );
            InputStream inputStream = getTemplateData( userSession, repository, md5, true );

            if ( inputStream != null )
            {
                return ( Context context, Result result ) -> {

                    result.addHeader( "Content-Disposition",
                            "attachment;filename=" + makeTemplateName( templateData ) );
                    result.addHeader( "Content-Type", "application/octet-stream" );
                    result.addHeader( "Content-Length", String.valueOf( templateData.getSize() ) );

                    ResponseStreams responseStreams = context.finalizeHeaders( result );

                    try ( OutputStream outputStream = responseStreams.getOutputStream() )
                    {
                        ByteStreams.copy( inputStream, outputStream );
                    }
                    catch ( IOException e )
                    {
                        LOGGER.error( "Failed to get renderable metadata by md5: " + md5 );
                    }
                };
            }
        }

        return null;
    }


    private InputStream getTemplateData( UserSession userSession, final String repository, final String md5,
                                         final boolean isKurjunClient ) throws IOException
    {
        ArtifactId id = repositoryManager.constructArtifactId( repository, ObjectType.TemplateRepo.getId(), md5 );


        if ( repository.equalsIgnoreCase( "public" ) )
        {
            return unifiedTemplateRepository.getPackageStream( id );
        }
        else
        {
            if ( checkRepoPermissions( userSession, repository, toId( md5, repository ), Permission.Read ) )
            {
                return repositoryFactory.createLocalTemplate( new KurjunContext( repository ) ).getPackageStream( id );
            }
        }
        return null;
    }


    private void getMissingFiles()
    {
        List<SerializableMetadata> templateList = localPublicTemplateRepository.listPackages();

        UnifiedRepository unifiedRepository = repositoryFactory.createUnifiedRepo();
        unifiedRepository.getRepositories().addAll( artifactContext.getRemoteTemplateRepositories() );

        List<SerializableMetadata> remoteTemplateList = unifiedRepository.listPackages();
    }


    @Override
    public TemplateData getTemplate( UserSession userSession, String repository, final String md5, String version,
                                     String search )
    {
        //************ CheckPermissions ************************************

        ArtifactId id = repositoryManager.constructArtifactId( repository, ObjectType.TemplateRepo.getId(), md5 );

        TemplateData templateData = ( TemplateData ) unifiedTemplateRepository.getPackageInfo( id );

        if ( templateData != null )
        {
            boolean allowed = true;
            //if not public, check for permissions
            if ( !templateData.getOwnerFprint().equals( "public" ) )
            {
                allowed =
                        checkRepoPermissions( userSession, templateData.getOwnerFprint(), ( templateData.getUniqId() ),
                                Permission.Read );
            }

            if ( allowed )
            {
                return templateData;
            }
        }
        return null;
    }


    @Override
    public String md5()
    {
        return getPublicRepository().md5();
    }


    private String toId( final String md5, String repo )
    {
        return repo + "." + md5;
    }


    private LocalRepository getRepo( String repo )
    {
        return repositoryFactory.createLocalTemplate( new KurjunContext( repo ) );
    }


    private LocalRepository getPublicRepository()
    {
        return repositoryFactory.createLocalTemplate( new KurjunContext( "public" ) );
    }


    private String makeTemplateName( SerializableMetadata metadata )
    {
        return metadata.getName() + "_" + metadata.getVersion() + ".tar.gz";
    }


    //*******************************************************************
    private boolean checkRepoPermissions( UserSession userSession, String repoId, String contentId, Permission perm )
    {
        return relationManager
                .checkObjectPermissions( userSession.getUser(), repoId, ObjectType.TemplateRepo.getId(), contentId,
                        ObjectType.Artifact.getId(), perm );
    }
    //*******************************************************************


    @Override
    public List<String> getRepoList()
    {
        List<String> repoList = repositoryService.getRepositoryContextList();
        repoList.remove( AptManagerServiceImpl.REPO_NAME );
        repoList.remove( RawManagerServiceImpl.DEFAULT_RAW_REPO_NAME );

        return repoList;
    }
}
