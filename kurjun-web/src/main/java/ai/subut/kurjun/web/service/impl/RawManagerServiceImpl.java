package ai.subut.kurjun.web.service.impl;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;

import ai.subut.kurjun.ar.CompressionType;
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
import ninja.utils.ResponseStreams;

import static com.google.common.base.Preconditions.checkNotNull;


public class RawManagerServiceImpl implements RawManagerService
{
    public static final String DEFAULT_RAW_REPO_NAME = "raw";

    private RepositoryFactory repositoryFactory;
    private LocalRawRepository localPublicRawRepository;
    private UnifiedRepository unifiedRepository;
    private ArtifactContext artifactContext;

    private UserSession userSession;

    @Inject
    IdentityManagerService identityManagerService;

    @Inject
    RelationManagerService relationManagerService;


    @Inject
    public RawManagerServiceImpl( final RepositoryFactory repositoryFactory, final ArtifactContext artifactContext )
    {
        this.repositoryFactory = repositoryFactory;
        this.artifactContext = artifactContext;

        _local();
        _unified();
    }


    private void _local()
    {
        this.localPublicRawRepository =
                this.repositoryFactory.createLocalRaw( new KurjunContext( DEFAULT_RAW_REPO_NAME ) );
    }


    private void _unified()
    {
        this.unifiedRepository = this.repositoryFactory.createUnifiedRepo();
        unifiedRepository.getRepositories().add( this.localPublicRawRepository );
        unifiedRepository.getRepositories().addAll( artifactContext.getRemoteRawRepositories() );
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
                        e.printStackTrace();
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
    public boolean delete( String repository, final byte[] md5 )
    {
        DefaultMetadata defaultMetadata = new DefaultMetadata();
        defaultMetadata.setFingerprint( repository );
        defaultMetadata.setMd5sum( md5 );
        try
        {
            String objectId = defaultMetadata.getId().toString();

            //***** Check permissions (DELETE) *****************
            if ( checkRepoPermissions( "raw", objectId, Permission.Delete ) )
            {
                relationManagerService
                        .removeRelationsByTrustObject( objectId, RelationObjectType.RepositoryContent.getId() );

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
    public Metadata put( final File file )
    {
        Metadata metadata = null;
        try
        {
            // *******CheckRepoOwner ***************
            relationManagerService.checkRelationOwner( userSession, "raw", RelationObjectType.RepositoryRaw.getId() );
            //**************************************

            //***** Check permissions (WRITE) *****************
            if ( checkRepoPermissions( "raw", null, Permission.Write ) )
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
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return metadata;
    }


    @Override
    public Metadata put( final File file, final String repository )
    {
        Metadata metadata = null;
        try
        {
            // *******CheckRepoOwner ***************
            relationManagerService.checkRelationOwner( userSession, "raw", RelationObjectType.RepositoryRaw.getId() );
            //**************************************

            //***** Check permissions (WRITE) *****************
            if ( checkRepoPermissions( "raw", null, Permission.Write ) )
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
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return metadata;
    }


    @Override
    public Metadata put( final File file, final String filename, final String repository )
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
            if ( checkRepoPermissions( "raw", null, Permission.Write ) )
            {
                LocalRawRepository localRawRepository = getLocalPublicRawRepository( new KurjunContext( repository ) );
                metadata = localRawRepository.put( file, filename, repository );

                //***** Build Relation ****************
                relationManagerService
                        .buildTrustRelation( userSession.getUser(), userSession.getUser(), metadata.getId().toString(),
                                RelationObjectType.RepositoryContent.getId(),
                                relationManagerService.buildPermissions( 4 ) );
                //*************************************
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return metadata;
    }


    public LocalRawRepository getLocalPublicRawRepository( KurjunContext context )
    {
        // *******CheckRepoOwner ***************
        relationManagerService
                .checkRelationOwner( userSession, context.getName(), RelationObjectType.RepositoryRaw.getId() );
        //**************************************

        return repositoryFactory.createLocalRaw( context );
    }


    @Override
    public List<SerializableMetadata> list( String repository )
    {
        switch ( repository )
        {
            //return local list
            case "public":
                return localPublicRawRepository.listPackages();
            //return unified repo list
            case "all":
                return unifiedRepository.listPackages();
            //return personal repository list
            default:
                return repositoryFactory.createLocalApt( new KurjunContext( repository ) ).listPackages();
        }
    }


    @Override
    public void setUserSession( UserSession userSession )
    {
        this.userSession = userSession;
    }


    public UserSession getUserSession()
    {
        return this.userSession;
    }


    //*******************************************************************
    private boolean checkRepoPermissions( String repoId, String contentId, Permission perm )
    {
        return relationManagerService
                .checkRepoPermissions( userSession, repoId, RelationObjectType.RepositoryRaw.getId(), contentId,
                        RelationObjectType.RepositoryContent.getId(), perm );
    }
    //*******************************************************************
}
