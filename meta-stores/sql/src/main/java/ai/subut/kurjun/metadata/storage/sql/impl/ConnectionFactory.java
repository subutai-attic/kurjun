package ai.subut.kurjun.metadata.storage.sql.impl;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


/**
 * This class provides connections to database. Connection properties are taken from {@link Properties} instance in
 * {@link init()} method. That properties instance should use property name as specified in
 * {@link ConnectionPropertyName}. There is sample {@code db.properties} file that can be loaded into {@link Properties}
 * instance. Data source class name specified in that properties file shall have a corresponding dependency in
 * {@code pom.xml} file.
 *
 */
class ConnectionFactory
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ConnectionFactory.class );

    private DataSource dataSource;


    private static class ConnectionFactoryHolder
    {
        private static final ConnectionFactory INSTANCE = new ConnectionFactory();
    }


    private ConnectionFactory()
    {
    }


    public static ConnectionFactory getInstance()
    {
        return ConnectionFactoryHolder.INSTANCE;
    }


    /**
     * Initiates connection pool to DB.
     *
     * @param properties connection properties, property names specified in {@link ConnectionPropertyName} must be used
     */
    public void init( Properties properties )
    {
        HikariConfig config = new HikariConfig( properties );
        dataSource = new HikariDataSource( config );
        LOGGER.info( "Kurjun SQL DB metadata store successfully initialized" );
    }


    /**
     * Gets a connection to db from the pool of connections. Every connection shall be closed after use.
     *
     * @return connection to db
     *
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException
    {
        return dataSource.getConnection();
    }


    /**
     * Closes connection pool so that all connections and related resources are released. Recommended to be used on
     * application exit.
     */
    public void close()
    {
        if ( dataSource instanceof HikariDataSource )
        {
            ( ( HikariDataSource ) dataSource ).close();
        }
    }

}

