package ai.subut.kurjun.metadata.storage.nosql;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;


/**
 * Provider class for {@link Session} instance. Needs to be initialized. If not initialized first get request triggers
 * initialization of session to localhost with default port.
 *
 */
public class CassandraConnector implements Provider<Session>
{
    private static final Logger LOGGER = LoggerFactory.getLogger( CassandraConnector.class );

    private Cluster cluster;
    private Session session;


    private CassandraConnector()
    {
    }


    public static CassandraConnector getInstance()
    {
        return CassandraConnectorHolder.INSTANCE;
    }


    @Override
    public Session get()
    {
        if ( session == null )
        {
            // try localhost with default port if not explicitly inited
            try
            {
                init( "localhost", 0 );
            }
            catch ( IOException ex )
            {
                throw new ProvisionException( "Failed to init Cassandra session", ex );
            }
        }
        return this.session;
    }


    /**
     * Initiates connections to Cassandra cluster.
     *
     * @param node the address of the node to connect to, any of the cluster nodes may be used
     * @param port the port to use; 0 for default
     *
     * @throws IOException
     */
    public void init( String node, int port ) throws IOException
    {
        Cluster.Builder clusterBuilder = Cluster.builder().addContactPoint( node );
        if ( port > 0 )
        {
            clusterBuilder.withPort( port );
        }

        cluster = clusterBuilder.build();
        LOGGER.info( "Connected to cluster: {}", cluster.getMetadata().getClusterName() );

        session = cluster.connect();
    }


    public void close()
    {
        if ( session != null )
        {
            session.close();
        }
        if ( cluster != null )
        {
            cluster.close();
        }
    }


    private static class CassandraConnectorHolder
    {
        private static final CassandraConnector INSTANCE = new CassandraConnector();
    }


}

