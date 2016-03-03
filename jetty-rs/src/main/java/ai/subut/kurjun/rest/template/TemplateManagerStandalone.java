package ai.subut.kurjun.rest.template;


import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Hex;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.inject.Injector;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.cfparser.ControlFileParserModule;
import ai.subut.kurjun.common.KurjunBootstrap;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.common.utils.InetUtils;
import ai.subut.kurjun.index.PackagesIndexParserModule;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.metadata.common.subutai.TemplateId;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreModule;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.repository.RemoteRepository;
import ai.subut.kurjun.model.repository.Repository;
import ai.subut.kurjun.model.repository.UnifiedRepository;
import ai.subut.kurjun.repo.LocalTemplateRepository;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.repo.RepositoryModule;
import ai.subut.kurjun.riparser.ReleaseIndexParserModule;
import ai.subut.kurjun.security.SecurityModule;
import ai.subut.kurjun.snap.SnapMetadataParserModule;
import ai.subut.kurjun.storage.factory.FileStoreFactory;
import ai.subut.kurjun.storage.factory.FileStoreModule;
import ai.subut.kurjun.subutai.SubutaiTemplateParserModule;
import io.subutai.common.protocol.TemplateKurjun;
import io.subutai.core.kurjun.api.template.TemplateRepository;
import io.subutai.core.kurjun.impl.TemplateManagerImpl;
import io.subutai.core.kurjun.impl.TrustedWebClientFactoryModule;
import io.subutai.core.kurjun.impl.model.RepoUrl;
import io.subutai.core.kurjun.impl.model.UserRepoContext;
import io.subutai.core.kurjun.impl.store.RepoUrlStore;
import io.subutai.core.kurjun.impl.store.UserRepoContextStore;


public class TemplateManagerStandalone
{

    private static final Logger LOGGER = LoggerFactory.getLogger( TemplateManagerImpl.class );

    private Set<UserRepoContext> GLOBAL_CONTEXTS;

    private final List<String> globalKurjunUrlList = new ArrayList<>();

    private Injector injector;

    private Set<RepoUrl> remoteRepoUrls = new HashSet<>();

    // private Set<RepoUrl> globalRepoUrls = new LinkedHashSet<>();
    private final RepoUrlStore repoUrlStore = new RepoUrlStore( null );

    private final UserRepoContextStore userRepoContextStore = new UserRepoContextStore( null );


    public TemplateManagerStandalone( String globalKurjunUrl )
    {
        parseGlobalKurjunUrls( globalKurjunUrl );
    }


    public TemplateManagerStandalone()
    {
    }


    public void init()
    {
        injector = bootstrapDI();

        KurjunProperties properties = injector.getInstance( KurjunProperties.class );

        initRepoUrls();

        initUserRepoContexts( properties );

        logAllUrlsInUse();
    }


    public void dispose()
    {

    }


    public TemplateKurjun getTemplate( String repository, byte[] md5, String templateOwner, boolean isKurjunClient )
            throws IOException
    {
        DefaultTemplate m = new DefaultTemplate();
        m.setId( templateOwner, md5 );

        UserRepoContext context = getUserRepoContext( repository );
        UnifiedRepository repo = getRepository( context, isKurjunClient );
        SubutaiTemplateMetadata meta = ( SubutaiTemplateMetadata ) repo.getPackageInfo( m );
        if ( meta != null )
        {
            return convertToSubutaiTemplate( meta );
        }
        return null;
    }


    public TemplateKurjun getTemplate( String repository, String name, String version, boolean isKurjunClient )
            throws IOException
    {
        DefaultMetadata m = new DefaultMetadata();
        m.setName( name );
        m.setVersion( version );

        UserRepoContext context = getUserRepoContext( repository );
        UnifiedRepository repo = getRepository( context, isKurjunClient );

        SubutaiTemplateMetadata meta = ( SubutaiTemplateMetadata ) repo.getPackageInfo( m );
        if ( meta != null )
        {
            return convertToSubutaiTemplate( meta );
        }
        return null;
    }


    public TemplateKurjun getTemplate( final String name )
    {
        try
        {
            return getTemplate( TemplateRepository.PUBLIC, name, null, false );
        }
        catch ( IOException e )
        {
            LOGGER.error( "Error in getTemplate(name)", e );

            return null;
        }
    }


    public InputStream getTemplateData( String repository, byte[] md5, String templateOwner, boolean isKurjunClient )
            throws IOException
    {
        UserRepoContext context = getUserRepoContext( repository );
        UnifiedRepository repo = getRepository( context, isKurjunClient );

        DefaultTemplate m = new DefaultTemplate();
        m.setId( templateOwner, md5 );
        InputStream is = repo.getPackageStream( m );

        if ( is != null )
        {
            return is;
        }
        return null;
    }


    public List<TemplateKurjun> list( String repository, boolean isKurjunClient ) throws IOException
    {
        UserRepoContext context = getUserRepoContext( repository );
        UnifiedRepository repo = getRepository( context, isKurjunClient );
        Set<SerializableMetadata> metadatas = listPackagesFromCache( repo );

        List<TemplateKurjun> result = new LinkedList<>();

        for ( SerializableMetadata metadata : metadatas )
        {
            DefaultTemplate templateMeta = ( DefaultTemplate ) metadata;
            result.add( convertToSubutaiTemplate( templateMeta ) );
        }

        return result;
    }


    public List<TemplateKurjun> list()
    {
        try
        {
            return list( TemplateRepository.PUBLIC, false );
        }
        catch ( IOException e )
        {
            LOGGER.error( "Error in list", e );
            return Lists.newArrayList();
        }
    }


    public String upload( String repository, InputStream inputStream ) throws IOException
    {
        UserRepoContext context = getUserRepoContext( repository );
        LocalTemplateRepository repo = getLocalRepository( context );

        try
        {
            SubutaiTemplateMetadata m =
                    ( SubutaiTemplateMetadata ) repo.put( inputStream, CompressionType.GZIP, context.getName() );
            TemplateId tid = new TemplateId( m.getOwnerFprint(), Hex.encodeHexString( m.getMd5Sum() ) );
            return tid.get();
        }
        catch ( IOException ex )
        {
            LOGGER.error( "Failed to put template", ex );
        }

        return null;
    }


    public boolean delete( String repository, String templateOwner, byte[] md5 ) throws IOException
    {
        UserRepoContext context = getUserRepoContext( repository );
        LocalRepository repo = getLocalRepository( context );

        try
        {
            TemplateId tid = new TemplateId( templateOwner, Hex.encodeHexString( md5 ) );
            repo.delete( tid.get(), md5 );
            return true;
        }
        catch ( IOException ex )
        {
            LOGGER.error( "Failed to delete template", ex );
            return false;
        }
    }


    public List<Map<String, Object>> getRemoteRepoUrls()
    {
        List<Map<String, Object>> urls = new ArrayList<>();
        try
        {
            for ( RepoUrl r : repoUrlStore.getRemoteTemplateUrls() )
            {
                Map<String, Object> map = new HashMap<>( 3 );
                map.put( "url", r.getUrl().toExternalForm() );
                map.put( "useToken", r.getToken() != null ? "yes" : "no" );
                map.put( "global", "no" );
                urls.add( map );
            }

            for ( RepoUrl r : getGlobalKurjunUrls() )
            {
                Map<String, Object> map = new HashMap<>( 3 );
                map.put( "url", r.getUrl().toExternalForm() );
                map.put( "useToken", r.getToken() != null ? "yes" : "no" );
                map.put( "global", "yes" );
                urls.add( map );
            }
        }
        catch ( IOException e )
        {
            LOGGER.error( "", e );
        }
        return urls;
    }


    public void addRemoteRepository( URL url, String token )
    {
        try
        {
            if ( url != null && !url.getHost().equals( getExternalIp() ) )
            {
                repoUrlStore.addRemoteTemplateUrl( new RepoUrl( url, token ) );

                remoteRepoUrls = repoUrlStore.getRemoteTemplateUrls();

                LOGGER.info( "Remote template host url is added: {}", url );
            }
            else
            {
                LOGGER.error( "Failed to add remote host url: {}", url );
            }
        }
        catch ( IOException ex )
        {
            LOGGER.error( "Failed to add remote host url: {}", url, ex );
        }
    }


    public void removeRemoteRepository( URL url )
    {
        if ( url != null )
        {
            try
            {
                RepoUrl r = repoUrlStore.removeRemoteTemplateUrl( new RepoUrl( url, null ) );
                if ( r != null )
                {
                    LOGGER.info( "Remote template host url is removed: {}", url );
                }
                else
                {
                    LOGGER.warn( "Failed to remove remote host url: {}. Either it does not exist or it is a global url",
                            url );
                }
                remoteRepoUrls = repoUrlStore.getRemoteTemplateUrls();
            }
            catch ( IOException e )
            {
                LOGGER.error( "Failed to remove remote host url: {}", url, e );
            }
        }
    }


    public Set<String> getRepositories()
    {
        Set<String> set = GLOBAL_CONTEXTS.stream().map( c -> c.getName() ).collect( Collectors.toSet() );
        set.add( TemplateRepository.SHARED );
        set.add( TemplateRepository.MY );
        return Collections.unmodifiableSet( set );
    }


    private String getExternalIp()
    {
        try
        {
            List<InetAddress> ips = InetUtils.getLocalIPAddresses();
            return ips.get( 0 ).getHostAddress();
        }
        catch ( SocketException | IndexOutOfBoundsException ex )
        {
            LOGGER.error( "Cannot get external ip. Returning null.", ex );
            return null;
        }
    }


    private Injector bootstrapDI()
    {
        KurjunBootstrap bootstrap = new KurjunBootstrap();
        bootstrap.addModule( new ControlFileParserModule() );
        bootstrap.addModule( new ReleaseIndexParserModule() );
        bootstrap.addModule( new PackagesIndexParserModule() );
        bootstrap.addModule( new SubutaiTemplateParserModule() );

        bootstrap.addModule( new FileStoreModule() );
        bootstrap.addModule( new PackageMetadataStoreModule() );
        bootstrap.addModule( new SnapMetadataParserModule() );

        bootstrap.addModule( new RepositoryModule() );
        bootstrap.addModule( new TrustedWebClientFactoryModule() );
        bootstrap.addModule( new SecurityModule() );

        bootstrap.boot();

        return bootstrap.getInjector();
    }


    private void initRepoUrls()
    {
        try
        {
            // Load remote repo urls from store
            remoteRepoUrls = repoUrlStore.getRemoteTemplateUrls();

            //            // Refresh global urls
            //            repoUrlStore.removeAllGlobalTemplateUrl();
            //            for ( String url : SystemSettings.getGlobalKurjunUrls() )
            //            {
            //                repoUrlStore.addGlobalTemplateUrl( new RepoUrl( new URL( url ), null ) );
            //            }
            //
            //            // Load global repo urls from store
            //            globalRepoUrls = repoUrlStore.getGlobalTemplateUrls();
        }
        catch ( IOException e )
        {
            LOGGER.error( "Failed to get remote repository URLs", e );
        }
    }


    private void initUserRepoContexts( KurjunProperties properties )
    {
        // init repo urls
        try
        {
            // Load user repository contexts from store
            GLOBAL_CONTEXTS = userRepoContextStore.getUserRepoContexts();

            // add default repository contexts
            // TODO: should we need to save default UserRepoContext ??
            GLOBAL_CONTEXTS.add( new UserRepoContext( TemplateRepository.PUBLIC, TemplateRepository.PUBLIC ) );
            GLOBAL_CONTEXTS.add( new UserRepoContext( TemplateRepository.TRUST, TemplateRepository.TRUST ) );

            // init common
            for ( UserRepoContext kc : GLOBAL_CONTEXTS )
            {
                Properties kcp = properties.getContextProperties( kc );
                kcp.setProperty( FileStoreFactory.TYPE, FileStoreFactory.FILE_SYSTEM );
                kcp.setProperty( PackageMetadataStoreModule.PACKAGE_METADATA_STORE_TYPE,
                        PackageMetadataStoreFactory.FILE_DB );
            }
        }
        catch ( IOException e )
        {
            LOGGER.error( "Failed to get user repository contexts", e );
        }
    }


    private LocalTemplateRepository getLocalRepository( KurjunContext context ) throws IOException
    {
        try
        {
            RepositoryFactory repositoryFactory = injector.getInstance( RepositoryFactory.class );

            return ( LocalTemplateRepository ) repositoryFactory.createLocalTemplate( context );
        }
        catch ( IllegalArgumentException ex )
        {
            throw new IOException( ex );
        }
    }


    private UnifiedRepository getRepository( KurjunContext context, boolean isKurjunClient ) throws IOException
    {
        RepositoryFactory repositoryFactory = injector.getInstance( RepositoryFactory.class );
        UnifiedRepository unifiedRepo = repositoryFactory.createUnifiedRepo();
        unifiedRepo.getRepositories().add( getLocalRepository( context ) );

        if ( !isKurjunClient )
        {
            for ( RepoUrl repoUrl : remoteRepoUrls )
            {
                unifiedRepo.getRepositories().add( repositoryFactory
                        .createNonLocalTemplate( repoUrl.getUrl().toString(), null, context.getName(),
                                repoUrl.getToken() ) );
            }

            // shuffle the global repo list to randomize and normalize usage of them
            List<RepoUrl> list = new ArrayList<>( getGlobalKurjunUrls() );
            Collections.shuffle( list );

            for ( RepoUrl repoUrl : list )
            {
                unifiedRepo.getSecondaryRepositories().add( repositoryFactory
                        .createNonLocalTemplate( repoUrl.getUrl().toString(), null, context.getName(),
                                repoUrl.getToken() ) );
            }
        }
        return unifiedRepo;
    }


    private List<RepoUrl> getGlobalKurjunUrls()
    {
        return Collections.emptyList();
        //        try
        //        {
        //            List<RepoUrl> list = new ArrayList<>();
        //            for ( String url : SystemSettings.getGlobalKurjunUrls() )
        //            {
        //                list.add( new RepoUrl( new URL( url ), null ) );
        //            }
        //            return list;
        //        }
        //        catch ( MalformedURLException e )
        //        {
        //            throw new IllegalArgumentException( "Invalid global kurjun url", e );
        //        }
    }


    /**
     * Gets user repository context for templates repository.
     *
     * @return user repository context instance
     *
     * @throws IllegalArgumentException if invalid/unknown repository value is supplied
     */
    private UserRepoContext getUserRepoContext( String repository )
    {
        Set<UserRepoContext> set = GLOBAL_CONTEXTS;
        for ( UserRepoContext c : set )
        {
            if ( c.getName().equals( repository ) )
            {
                return c;
            }
        }

        throw new IllegalArgumentException( "Invalid repository " + repository );
    }


    private void parseGlobalKurjunUrls( String globalKurjunUrl )
    {
        if ( !Strings.isNullOrEmpty( globalKurjunUrl ) )
        {
            String urls[] = globalKurjunUrl.split( "," );

            for ( int x = 0; x < urls.length; x++ )
            {
                urls[x] = urls[x].trim();
                globalKurjunUrlList.add( urls[x] );
            }
        }
    }


    private void logAllUrlsInUse()
    {
        LOGGER.info( "Remote template urls:" );
        for ( RepoUrl r : remoteRepoUrls )
        {
            LOGGER.info( r.toString() );
        }

        for ( RepoUrl r : getGlobalKurjunUrls() )
        {
            LOGGER.info( r.toString() );
        }
    }


    /**
     * Gets cached metadata from the repositories of the supplied unified repository.
     */
    private Set<SerializableMetadata> listPackagesFromCache( UnifiedRepository repository )
    {
        Set<SerializableMetadata> result = new HashSet<>();

        Set<Repository> repos = new HashSet<>();
        repos.addAll( repository.getRepositories() );
        repos.addAll( repository.getSecondaryRepositories() );

        for ( Repository repo : repos )
        {
            if ( repo instanceof RemoteRepository )
            {
                RemoteRepository remote = ( RemoteRepository ) repo;
                List<SerializableMetadata> ls = remote.getMetadataCache().getMetadataList();
                result.addAll( ls );
            }
            else
            {
                List<SerializableMetadata> ls = repo.listPackages();
                result.addAll( ls );
            }
        }
        return result;
    }


    /**
     * Refreshes metadata cache for each remote repository.
     */
    private void refreshMetadataCache( String repository )
    {
        Set<RemoteRepository> remotes = new HashSet<>();
        RepositoryFactory repoFactory = injector.getInstance( RepositoryFactory.class );

        for ( RepoUrl url : remoteRepoUrls )
        {
            remotes.add(
                    repoFactory.createNonLocalTemplate( url.getUrl().toString(), null, repository, url.getToken() ) );
        }
        for ( RepoUrl url : getGlobalKurjunUrls() )
        {
            remotes.add(
                    repoFactory.createNonLocalTemplate( url.getUrl().toString(), null, repository, url.getToken() ) );
        }

        for ( RemoteRepository remote : remotes )
        {
            remote.getMetadataCache().refresh();
        }
    }


    private TemplateKurjun convertToSubutaiTemplate( SubutaiTemplateMetadata meta )
    {
        TemplateKurjun template =
                new TemplateKurjun( String.valueOf( meta.getId() ), Hex.encodeHexString( meta.getMd5Sum() ),
                        meta.getName(), meta.getVersion(), meta.getArchitecture().name(), meta.getParent(),
                        meta.getPackage(), meta.getOwnerFprint() );
        template.setConfigContents( meta.getConfigContents() );
        template.setPackagesContents( meta.getPackagesContents() );
        return template;
    }
}
