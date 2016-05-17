package ai.subut.kurjun.web.service.impl;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.ErrorCode;
import ai.subut.kurjun.common.service.KurjunContext;
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
import ai.subut.kurjun.web.service.RelationManagerService;
import ai.subut.kurjun.web.utils.Utils;
import ninja.Renderable;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.utils.ResponseStreams;

import static com.google.common.base.Preconditions.checkNotNull;


@Singleton
public class RawManagerServiceImpl implements RawManagerService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( RawManagerServiceImpl.class );

    public static final String DEFAULT_RAW_REPO_NAME = "raw";

    private RepositoryFactory repositoryFactory;
    private LocalRawRepository localPublicRawRepository;
    private UnifiedRepository unifiedRepository;
    private ArtifactContext artifactContext;

    @Inject
    IdentityManagerService identityManagerService;

    @Inject
    RelationManagerService relationManagerService;


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
                        LOGGER.error( " ***** Error on getting Raw file:" ,e);
                    }
                };
            }
        }
        return null;
    }


    @Override
    public String md5()
    {
        return Utils.MD5.toString( localPublicRawRepository.md5() );
    }


    @Override
    public Renderable getFile( String repository, final byte[] md5 )
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
                        LOGGER.error( " ***** Error on getting Raw file:", e );
                    }
                };
            }
        }

        return null;
    }


    @Override
    public Renderable getFile( final byte[] md5, final boolean isKurjun )
    {
        return getFile( DEFAULT_RAW_REPO_NAME, md5 );
    }


    @Override
    public int delete( UserSession userSession, String repository, final byte[] md5 )
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
                relationManagerService
                        .removeRelationsByTrustObject( objectId, RelationObjectType.RepositoryContent.getId() );

                if(localPublicRawRepository.delete( defaultMetadata.getId(), md5 ))
                {
                    return ErrorCode.Success.getId();
                }
            }
            else
            {
                return ErrorCode.AccessPermissionError.getId();
            }
        }
        catch ( Exception e )
        {
            LOGGER.error( " ***** Error on deleting Raw file:" ,e);
        }
        return ErrorCode.SystemError.getId();
    }


    @Override
    public SerializableMetadata getInfo( final byte[] md5 )
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
            relationManagerService.checkRelationOwner( userSession, "raw", RelationObjectType.RepositoryRaw.getId() );
            //**************************************

            //***** Check permissions (WRITE) *****************
            if ( checkRepoPermissions( userSession, "raw", null, Permission.Write ) )
            {
                metadata = localPublicRawRepository.put( file, CompressionType.NONE, DEFAULT_RAW_REPO_NAME );

                //***** Build Relation ****************
                relationManagerService
                        .buildTrustRelation( userSession.getUser(), userSession.getUser(), metadata.getId().toString(),
                                RelationObjectType.RepositoryContent.getId(),
                                relationManagerService.buildPermissions( 4 ) );
                //*************************************
            }
        }
        catch ( Exception e )
        {
            LOGGER.error( " ***** Error on uploading Raw file:", e );
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
            relationManagerService.checkRelationOwner( userSession, "raw", RelationObjectType.RepositoryRaw.getId() );
            //**************************************

            //***** Check permissions (WRITE) *****************
            if ( checkRepoPermissions( userSession, "raw", null, Permission.Write ) )
            {
                metadata =
                        localPublicRawRepository.put( new FileInputStream( file ), CompressionType.NONE, repository );

                //***** Build Relation ****************
                relationManagerService
                        .buildTrustRelation( userSession.getUser(), userSession.getUser(), metadata.getId().toString(),
                                RelationObjectType.RepositoryContent.getId(),
                                relationManagerService.buildPermissions( 4 ) );
                //*************************************
            }
        }
        catch ( Exception e )
        {
            LOGGER.error( " ***** Error on uploading Raw file:", e );
        }
        return metadata;
    }


    //*******************************************************************
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
            relationManagerService.checkRelationOwner( userSession, "raw", RelationObjectType.RepositoryRaw.getId() );
            //**************************************

            //***** Check permissions (WRITE) *****************
            if ( checkRepoPermissions( userSession, "raw", null, Permission.Write ) )
            {
                LocalRawRepository localRawRepository =
                        getLocalPublicRawRepository( userSession, new KurjunContext( repository ) );
                metadata = localRawRepository.put( file, filename, repository );

                //***** Build Relation ****************
                relationManagerService
                        .buildTrustRelation( userSession.getUser(), userSession.getUser(), metadata.getId().toString(),
                                RelationObjectType.RepositoryContent.getId(),
                                relationManagerService.buildPermissions( 4 ) );
                //*************************************
            }
        }
        catch ( Exception e )
        {
            LOGGER.error( " ***** Error on uploading Raw file:", e );
        }
        return metadata;
    }


    //*******************************************************************
    public LocalRawRepository getLocalPublicRawRepository( UserSession userSession, KurjunContext context )
    {
        // *******CheckRepoOwner ***************

        try
        {
            relationManagerService
                    .checkRelationOwner( userSession, context.getName(), RelationObjectType.RepositoryRaw.getId() );
            //**************************************

            return repositoryFactory.createLocalRaw( context );
        }
        catch ( Exception e )
        {
            LOGGER.error( " ***** Error on getLocalPublicRawRepository:", e );
        }

        return null;
    }


    //*******************************************************************
    @Override
    public List<SerializableMetadata> list( String repository )
    {
        try
        {   List<SerializableMetadata> results;

            switch ( repository )
            {
                //return local list
                case "local":
                    results = localPublicRawRepository.listPackages();
                //return unified repo list
                case "all":
                    results = unifiedRepository.listPackages();
                //return personal repository list
                default:
                    results = repositoryFactory.createLocalApt( new KurjunContext( repository ) ).listPackages();
            }

            return results;

        }
        catch(Exception e)
        {
            LOGGER.error( " ***** Error on getting all raw files:", e );
        }

        return null;
    }


    //*******************************************************************
    private boolean checkRepoPermissions( UserSession userSession, String repoId, String contentId, Permission perm )
    {
        return relationManagerService
                .checkRepoPermissions( userSession, repoId, RelationObjectType.RepositoryRaw.getId(), contentId,
                        RelationObjectType.RepositoryContent.getId(), perm );
    }
    //*******************************************************************
}
