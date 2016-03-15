package ai.subut.kurjun.web.init;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.repository.RemoteRepository;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.web.context.ArtifactContext;
import ai.subut.kurjun.web.model.RepositoryCache;
import ai.subut.kurjun.web.model.UserContext;
import ai.subut.kurjun.web.model.UserContextImpl;
import ai.subut.kurjun.web.service.UserRepoContextStore;


@Singleton
public class KurjunInitializer
{
    private static Logger LOGGER = LoggerFactory.getLogger( KurjunInitializer.class );


    private ArtifactContext artifactContext;

    private RepositoryFactory repositoryFactory;
    private UserRepoContextStore userRepoContextStore;
    private KurjunProperties kurjunProperties;
    private Set<LocalRepository> localRepositories;

    private Set<RemoteRepository> remoteRepositories;
    private Set<UserContext> userContextSet;


    @Inject
    public KurjunInitializer( UserRepoContextStore userRepoContextStore, RepositoryFactory repositoryFactory,
                              ArtifactContext artifactContext, final KurjunProperties kurjunProperties )
    {
        this.userRepoContextStore = userRepoContextStore;
        this.repositoryFactory = repositoryFactory;
        this.artifactContext = artifactContext;
        this.kurjunProperties = kurjunProperties;

        this.localRepositories = new HashSet<>();
        this.userContextSet = new HashSet<>();
        this.remoteRepositories = new HashSet<>();

        init();
    }


    private boolean init()
    {
        try
        {
            userContextSet = userRepoContextStore.getUserRepoContexts();
        }
        catch ( IOException e )
        {

            LOGGER.error( "Error while loading User Repositories: {}", e.getMessage() );

            System.exit( 1 );
        }
        //no user context found
        if ( userContextSet.size() > 0 )
        {
            return loadRepositories();
        }

        remoteRepositories();

        return false;
    }


    private boolean loadRepositories()
    {
        LOGGER.debug( "Indexing Kurjun Repositories" );

        userContextSet.stream().forEach( userContext -> {

            LOGGER.debug( "Adding {} ", userContext.getName() );

            localRepositories.add( repositoryFactory.createLocalTemplate( userContext ) );
        } );

        //add vapt repo
        localRepositories.add( repositoryFactory.createLocalApt( new UserContextImpl( "vapt" ) ) );

        LOGGER.debug( "Complete loading local template repositories. Found repositories: {}",
                localRepositories.size() );

        //if there are repos, index artifacts
        if ( localRepositories.size() > 0 )
        {
            indexArtifacts();
        }
        return false;
    }


    private boolean remoteRepositories()
    {
        String sourceList = kurjunProperties.get( KurjunConstants.KURJUN_SOURCE_LIST );

        Properties properties = new Properties();

        String[] servers;

        if ( sourceList != null )
        {
            try
            {
                InputStream inputStream = new FileInputStream( sourceList );
                properties.load( inputStream );
                servers = properties.getProperty( KurjunConstants.KURJUN_SERVER_LIST ).split( "," );

                if ( servers.length > 0 )
                {
                    for ( String s : servers )
                    {
                        artifactContext.addRemoteTemplateRepository(
                                repositoryFactory.createNonLocalTemplate( s, null, "public", null ) );

                        artifactContext.addRemoteRawRepositories( repositoryFactory.createNonLocalRaw( s, null ) );

                        artifactContext.addRemoteAptRepositories( repositoryFactory.createNonLocalApt( new URL( s ) ) );
                    }
                }

            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }

        return false;
    }


    private void fetch( Set<RemoteRepository> remoteRepository )
    {
        for ( RemoteRepository repo : remoteRepository )
        {
            Thread thread = new Thread( () -> {
                artifactContext.store( repo.getHostname(), new RepositoryCache( repo.getMd5(), repo.listPackages() ) );
            } );

            thread.start();
        }
    }


    private void indexArtifacts()
    {
        LOGGER.debug( "Indexing Kurjun Artifacts" );

        localRepositories.forEach( ( localRepository ) -> {

            List<SerializableMetadata> serializableMetadatas = localRepository.listPackages();

            serializableMetadatas.forEach( serializableMetadata -> artifactContext
                    .store( serializableMetadata.getMd5Sum(), ( KurjunContext ) localRepository.getContext() ) );
        } );
    }


    private boolean indexSharedArtifacts()
    {
        LOGGER.debug( "Indexing Kurjun Shared Artifacts" );

        return false;
    }


}
