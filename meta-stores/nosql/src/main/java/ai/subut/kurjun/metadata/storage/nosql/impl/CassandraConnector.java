package ai.subut.kurjun.metadata.storage.nosql.impl;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;


class CassandraConnector
{
    private static final Logger LOGGER = LoggerFactory.getLogger( CassandraConnector.class );

    private Cluster cluster;
    private Session session;
    private File replicationConfigFile;


    private CassandraConnector()
    {
    }


    public static CassandraConnector getInstance()
    {
        return CassandraConnectorHolder.INSTANCE;
    }


    public Session getSession()
    {
        return this.session;
    }


    public File getReplicationConfigFile()
    {
        return replicationConfigFile;
    }


    public void setReplicationConfigFile( File replicationConfigFile )
    {
        this.replicationConfigFile = replicationConfigFile;
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

        createSchema();
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


    private void createSchema() throws IOException
    {
        try ( InputStream is = getReplicationConfigStream() )
        {
            session.execute( SchemaInfo.getCreateKeyspaceStatement( is ) );
            session.execute( SchemaInfo.getCreateTableStatement() );
        }
    }


    private InputStream getReplicationConfigStream() throws FileNotFoundException
    {
        if ( replicationConfigFile != null )
        {
            return new FileInputStream( replicationConfigFile );
        }
        else
        {
            return ClassLoader.getSystemResourceAsStream( "cassandra-replication" );
        }
    }


    private static class CassandraConnectorHolder
    {
        private static final CassandraConnector INSTANCE = new CassandraConnector();
    }


}

