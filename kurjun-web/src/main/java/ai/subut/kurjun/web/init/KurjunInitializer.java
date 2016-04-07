package ai.subut.kurjun.web.init;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.web.context.ArtifactContext;
import ai.subut.kurjun.web.service.UserRepoContextStore;


@Singleton
public class KurjunInitializer
{
    private static Logger LOGGER = LoggerFactory.getLogger( KurjunInitializer.class );


    private ArtifactContext artifactContext;
    private RepositoryFactory repositoryFactory;
    private KurjunProperties kurjunProperties;


    @Inject
    public KurjunInitializer( UserRepoContextStore userRepoContextStore, RepositoryFactory repositoryFactory,
                              ArtifactContext artifactContext, final KurjunProperties kurjunProperties )
    {

        this.repositoryFactory = repositoryFactory;
        this.artifactContext = artifactContext;
        this.kurjunProperties = kurjunProperties;
        LOGGER.debug( "Starting Kurjun Remote Repo Initializer" );
        init();
    }


    private boolean init()
    {
        return remoteRepositories();
    }


    private boolean remoteRepositories()
    {
        String sourceList = kurjunProperties.get( KurjunConstants.KURJUN_SOURCE_LIST );
        LOGGER.debug( "Getting sources list" );
        Properties properties = new Properties();

        String[] servers;

        if ( sourceList != null )
        {
            try
            {
                if ( new File( sourceList ).exists() )
                {
                    LOGGER.debug( "Getting sources list from {}", sourceList );

                    InputStream inputStream = new FileInputStream( sourceList );

                    properties.load( inputStream );
                    String serverList = properties.getProperty( KurjunConstants.KURJUN_SERVER_LIST );

                    if ( serverList != null )
                    {
                        LOGGER.debug( "Kurjun servers {}", serverList );
                        servers = serverList.split( "," );
                        if ( servers.length > 0 )
                        {
                            for ( String s : servers )
                            {
                                artifactContext.addRemoteTemplateRepository(
                                        repositoryFactory.createNonLocalTemplate( s, null, "public", null, "local" ) );

                                artifactContext.addRemoteRawRepositories(
                                        repositoryFactory.createNonLocalRaw( s, null, "local" ) );

                                artifactContext.addRemoteAptRepositories(
                                        repositoryFactory.createNonLocalApt( new URL( s ) ) );
                            }
                        }
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
}
