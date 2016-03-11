package ai.subut.kurjun.web.init;


import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.web.context.ArtifactContext;
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

    private Set<LocalRepository> localRepositories;
    private Set<UserContext> userContextSet;


    @Inject
    public KurjunInitializer( UserRepoContextStore userRepoContextStore,
                              RepositoryFactory repositoryFactory,
                              ArtifactContext artifactContext )
    {
        this.userRepoContextStore = userRepoContextStore;
        this.repositoryFactory = repositoryFactory;
        this.artifactContext = artifactContext;

        this.localRepositories = new HashSet<>();
        this.userContextSet = new HashSet<>();

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
