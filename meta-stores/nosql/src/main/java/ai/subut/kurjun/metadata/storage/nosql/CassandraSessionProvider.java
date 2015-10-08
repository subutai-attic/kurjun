package ai.subut.kurjun.metadata.storage.nosql;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.service.KurjunProperties;


/**
 * Provider class for {@link Session} instance. Connection parameters are taken from Kurjun properties. If node or port
 * are not specified localhost or default port are used respectively.
 *
 */
@Singleton
public class CassandraSessionProvider
{
    public static final String CASSANDRA_NODE = "metadata.store.cassandra.node";
    public static final String CASSANDRA_PORT = "metadata.store.cassandra.port";

    private static final Logger LOGGER = LoggerFactory.getLogger( CassandraSessionProvider.class );

    private Cluster cluster;
    private Session session;

    private String node;
    private int port;

    private final Lock lock = new ReentrantLock();
    private boolean initialized = false;


    @Inject
    public CassandraSessionProvider( KurjunProperties kurjunProperties )
    {
        this.node = kurjunProperties.get( CASSANDRA_NODE );
        this.port = kurjunProperties.getIntegerWithDefault( CASSANDRA_PORT, 0 );
    }


    CassandraSessionProvider( String node, int port )
    {
        this.node = node;
        this.port = port;
    }


    public Session get()
    {
        if ( !initialized )
        {
            lock.lock();
            try
            {
                if ( !initialized )
                {
                    init( node, port );
                    initialized = true;
                }
            }
            finally
            {
                lock.unlock();
            }
        }
        return this.session;
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


    /**
     * Initiates connections to Cassandra cluster.
     *
     * @param node the address of the node to connect to, any of the cluster nodes may be used
     * @param port the port to use; 0 for default
     *
     */
    private void init( String node, int port )
    {
        if ( node == null )
        {
            LOGGER.warn( "Cassandra node property '{}' not set, using localhost", CASSANDRA_NODE );
            node = "localhost";
        }

        Cluster.Builder clusterBuilder = Cluster.builder().addContactPoint( node );
        if ( port > 0 )
        {
            clusterBuilder.withPort( port );
        }

        cluster = clusterBuilder.build();
        LOGGER.info( "Connected to cluster: {}", cluster.getMetadata().getClusterName() );

        session = cluster.connect();
    }


}

