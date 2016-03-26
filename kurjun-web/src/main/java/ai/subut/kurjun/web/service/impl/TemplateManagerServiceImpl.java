package ai.subut.kurjun.web.service.impl;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Hex;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.metadata.common.subutai.TemplateId;
import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.RelationObjectType;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.repository.UnifiedRepository;
import ai.subut.kurjun.repo.LocalTemplateRepository;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.web.context.ArtifactContext;
import ai.subut.kurjun.web.model.UserContextImpl;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ai.subut.kurjun.web.service.RelationManagerService;
import ai.subut.kurjun.web.service.RepositoryService;
import ai.subut.kurjun.web.service.TemplateManagerService;
import ai.subut.kurjun.web.utils.Utils;
import ninja.Context;
import ninja.Renderable;
import ninja.Result;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.utils.ResponseStreams;


public class TemplateManagerServiceImpl implements TemplateManagerService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( TemplateManagerServiceImpl.class );

    //------------------------------
    @Inject
    IdentityManagerService identityManagerService;

    @Inject
    RelationManagerService relationManagerService;

    @Inject
    RepositoryService repositoryService;
    //------------------------------

    private RepositoryFactory repositoryFactory;
    private ArtifactContext artifactContext;
    private LocalTemplateRepository localPublicTemplateRepository;
    private UnifiedRepository unifiedTemplateRepository;

    private UserSession userSession;


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
        this.localPublicTemplateRepository =
                ( LocalTemplateRepository ) repositoryFactory.createLocalTemplate( new KurjunContext( "public" ) );
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
    public SerializableMetadata getTemplate( final byte[] md5 ) throws IOException
    {
        KurjunContext context = artifactContext.getRepository( new BigInteger( 1, md5 ).toString( 16 ) );

        if(checkRepoPermissions( context.getName() , toId(md5, context.getName() ), Permission.Write ))
        {
            DefaultTemplate defaultTemplate = new DefaultTemplate();
            defaultTemplate.setId( context.getName(), md5 );
            return repositoryFactory.createLocalTemplate( context ).getPackageInfo( defaultTemplate );
        }

        return null;
    }


    @Override
    public InputStream getTemplateData( final String repository, final byte[] md5, final boolean isKurjunClient )
            throws IOException
    {
        if(checkRepoPermissions( repository, toId(md5, repository ), Permission.Write ))
        {
            DefaultTemplate defaultTemplate = new DefaultTemplate();
            defaultTemplate.setId( repository, md5 );

            if ( repository.equalsIgnoreCase( "public" ) )
            {
                return unifiedTemplateRepository.getPackageStream( defaultTemplate );
            }
            else
            {
                return repositoryFactory.createLocalTemplate( new KurjunContext( repository ) )
                                        .getPackageStream( defaultTemplate );
            }
        }
        return null;
    }


    @Override
    public List<SerializableMetadata> list( final String repository, final boolean isKurjunClient ) throws IOException
    {
        List<SerializableMetadata> results;

        switch ( repository )
        {
            //return local list
            case "public":
                results = localPublicTemplateRepository.listPackages();
                //return personal repository list
                break;
            case "all":
                results = unifiedTemplateRepository.listPackages();
                //return unified repo list
                break;
            default:
                results = unifiedTemplateRepository.listPackages();
                results.addAll( repositoryFactory.createLocalTemplate( new KurjunContext( repository ) ).listPackages() );
                for ( String repo :  repositoryService.getRepositories() ) {
                    if ( repo.equals( repository ) )
                    {
                        LocalRepository localRepo = repositoryFactory.createLocalTemplate( new KurjunContext( repo ) );
                        for ( SerializableMetadata sm : localRepo.listPackages() )
                        {
                            if ( !results.contains( sm ) )
                            {
                                results.add( sm );
                            }
                        }
                    }
                }
                //results.addAll( repositoryService.getRepositories() );
                //results = repositoryFactory.createLocalTemplate( new KurjunContext( repository ) ).listPackages();
        }

        if ( checkRepoPermissions( repository, null, Permission.Read ))
        {
            return results;
        }
        else
        {
            //****CheckPermissions *************
            for ( Iterator<SerializableMetadata> iterator = results.iterator(); iterator.hasNext(); )
            {
                final SerializableMetadata mdata = iterator.next();

                //***** Check permissions (WRITE) *****************
                if ( !relationManagerService.checkUserPermissions( userSession, mdata.getId().toString(),
                        RelationObjectType.RepositoryContent.getId() ).contains( Permission.Read ) )
                {
                    iterator.remove();
                }
            }
        }

        //**********************************

        return results;
    }


    @Override
    public String upload( final String repository, final InputStream inputStream ) throws IOException
    {

        if(userSession.getUser().equals( identityManagerService.getPublicUser() ))
            return null;

        // *******CheckRepoOwner ***************
        relationManagerService
                .checkRelationOwner( userSession, repository, RelationObjectType.RepositoryTemplate.getId() );
        //**************************************

        //***** Check permissions (WRITE) *****************
        if ( checkRepoPermissions( repository, null, Permission.Write ) )
        {
            SubutaiTemplateMetadata metadata = ( SubutaiTemplateMetadata ) getRepo( repository )
                    .put( inputStream, CompressionType.GZIP, repository );

            if ( metadata != null )
            {
                if ( metadata.getMd5Sum() != null )
                {
                    artifactContext.store( metadata.getMd5Sum(), new KurjunContext( repository ) );
                    String templateId = repository + "." + Hex.encodeHexString( metadata.getMd5Sum() );

                    //***** Build Relation ****************
                    relationManagerService.buildTrustRelation( userSession.getUser(), userSession.getUser(), templateId,
                            RelationObjectType.RepositoryContent.getId(),
                            relationManagerService.buildPermissions( 4 ) );
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
    public String upload( final String repository, final File file ) throws IOException
    {

        if(userSession.getUser().equals( identityManagerService.getPublicUser() ))
            return null;

        // *******CheckRepoOwner ***************
        relationManagerService
                .checkRelationOwner( userSession, repository, RelationObjectType.RepositoryTemplate.getId() );
        //**************************************

        //***** Check permissions (WRITE) *****************
        if ( checkRepoPermissions( repository, null, Permission.Write ) )
        {
            SubutaiTemplateMetadata metadata =
                    ( SubutaiTemplateMetadata ) getRepo( repository ).put( file, CompressionType.GZIP, repository );

            if ( metadata != null )
            {
                if ( metadata.getMd5Sum() != null )
                {
                    String templateId = toId( metadata != null ? metadata.getMd5Sum() : new byte[0], repository );
                    artifactContext.store( metadata.getMd5Sum(), new UserContextImpl( repository ) );

                    //***** Build Relation ****************
                    relationManagerService.buildTrustRelation( userSession.getUser(), userSession.getUser(), templateId,
                            RelationObjectType.RepositoryContent.getId(),
                            relationManagerService.buildPermissions( 4 ) );
                    //*************************************
                    return templateId;
                }
            }
        }
        return null;
    }


    @Override
    public boolean delete( TemplateId tid ) throws IOException
    {
        //************ CheckPermissions ************************************
        if ( checkRepoPermissions( tid.getOwnerFprint(), tid.get(), Permission.Delete  ) )
        {
            LocalTemplateRepository _repository = ( LocalTemplateRepository ) getRepo( tid.getOwnerFprint() );

            // remove Relation
            relationManagerService.removeRelationsByTrustObject( tid.get(), RelationObjectType.RepositoryContent.getId() );

            return _repository.delete( tid.get(), Utils.MD5.toByteArray( tid.getMd5() ) );
        }
        else
        {
            return false;
        }
    }


    @Override
    public LocalRepository createUserRepository( final KurjunContext userName )
    {
        return repositoryFactory.createLocalTemplate( userName );
    }


    @Override
    public void shareTemplate( final String templateId, final String targetUserName )
    {

    }


    @Override
    public Renderable renderableTemplate( final String repository, String md5, final boolean isKurjunClient )
            throws IOException
    {

        //************ CheckPermissions ************************************
        if ( checkRepoPermissions( repository, repository + "." + md5, Permission.Read  ) )
        {
            DefaultTemplate defaultTemplate = new DefaultTemplate();
            defaultTemplate.setId( repository, Utils.MD5.toByteArray( md5 ) );

            DefaultTemplate dt = null;

            List<SerializableMetadata> templateList = list( repository, true);
            for ( SerializableMetadata sm : templateList ) {
                if ( Utils.MD5.toString( sm.getMd5Sum() ).equals( md5 ) ) {
                    dt = (DefaultTemplate ) sm;
                    break;
                }
            }

            DefaultTemplate metadata = dt;// ( DefaultTemplate ) unifiedTemplateRepository.getPackageInfo( defaultTemplate );

            InputStream inputStream = getTemplateData( repository, Utils.MD5.toByteArray( md5 ), false );

            if ( inputStream != null )
            {
                return ( Context context, Result result ) -> {

                    result.addHeader( "Content-Disposition", "attachment;filename=" + makeTemplateName( metadata ) );
                    result.addHeader( "Content-Type", "application/octet-stream" );
                    result.addHeader( "Content-Length", String.valueOf( metadata.getSize() ) );

                    ResponseStreams responseStreams = context.finalizeHeaders( result );

                    try ( OutputStream outputStream = responseStreams.getOutputStream() )
                    {
                        ByteStreams.copy( inputStream, outputStream );
                    }
                    catch ( IOException e )
                    {
                        LOGGER.error( "Failed to get renderable template by md5: " + md5 );
                    }
                };
            }
        }

        return null;
    }


    @Override
    public DefaultTemplate getTemplate( final TemplateId templateId, final String md5, String name, String version )
    {
        //************ CheckPermissions ************************************
        if ( checkRepoPermissions( templateId.getOwnerFprint(), templateId.get(), Permission.Read  ) )
        {
            DefaultTemplate defaultTemplate = new DefaultTemplate();

            if ( templateId != null )
            {
                defaultTemplate.setId( templateId.getOwnerFprint(), Utils.MD5.toByteArray( templateId.getMd5() ) );
            }

            defaultTemplate.setName( name );
            defaultTemplate.setVersion( version );
            String[] repo_and_id = templateId.get().split( "\\." );

            try {
                List<SerializableMetadata> templList = list( repo_and_id[0], true );
                Iterator iter = templList.iterator();
                while ( iter.hasNext() ) {
                    DefaultTemplate df = (DefaultTemplate) iter.next();
                    if ( df.equals( defaultTemplate )) {
                        return df;
                    }
                }
            }
            catch ( IOException e ) {

            }
            return ( DefaultTemplate ) unifiedTemplateRepository.getPackageInfo( defaultTemplate );
        }
        else
        {
            return null;
        }
    }


    @Override
    public boolean downloadTemplates()
    {
        DefaultTemplate defaultTemplate = new DefaultTemplate();
        defaultTemplate.setName( "master" );

        final Metadata[] loaded = new DefaultTemplate[1];

        if ( localPublicTemplateRepository.getPackageInfo( defaultTemplate ) == null )
        {
            Thread thread = new Thread( () -> {

                InputStream inputStream = unifiedTemplateRepository.getPackageStream( defaultTemplate );

                if ( inputStream != null )
                {
                    try
                    {
                        loaded[0] = ( DefaultTemplate ) localPublicTemplateRepository
                                .put( inputStream, CompressionType.GZIP, "public" );
                    }
                    catch ( IOException e )
                    {
                        e.printStackTrace();
                    }
                }
            } );

            thread.run();

            if ( loaded[0] instanceof DefaultTemplate )
            {
                return true;
            }
        }
        return false;
    }


    @Override
    public String md5()
    {
        return Utils.MD5.toString( getPublicRepository().md5() );
    }


    @Override
    public List<Map<String, Object>> getSharedTemplateInfos( final byte[] md5, final String templateOwner )
            throws IOException
    {
        return null;
    }


    @Override
    public List<Map<String, Object>> listAsSimple( final String repository ) throws IOException
    {

        return null;
    }


    @Override
    public List<SerializableMetadata> list()
    {
        return null;
    }


    @Override
    public List<Map<String, Object>> getRemoteRepoUrls()
    {
        return null;
    }


    @Override
    public void addRemoteRepository( final URL url, final String token )
    {

    }


    @Override
    public void removeRemoteRepository( final URL url )
    {

    }


    @Override
    public void unshareTemplate( final String templateId, final String targetUserName )
    {

    }


    @Override
    public Set<String> getRepositories()
    {
        return null;
    }


    private String toId( final byte[] md5, String repo )
    {
        String hash = new BigInteger( 1, Arrays.copyOf( md5, md5.length ) ).toString( 16 );

        return repo + "." + hash;
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


    @Override
    public void setUserSession( UserSession userSession )
    {
        this.userSession = userSession;
    }


    //*******************************************************************
    private boolean checkRepoPermissions( String repoId, String contentId, Permission perm )
    {
        return relationManagerService
                .checkRepoPermissions( userSession, repoId, RelationObjectType.RepositoryTemplate.getId(), contentId,
                        RelationObjectType.RepositoryContent.getId(), perm );

    }
    //*******************************************************************
}
