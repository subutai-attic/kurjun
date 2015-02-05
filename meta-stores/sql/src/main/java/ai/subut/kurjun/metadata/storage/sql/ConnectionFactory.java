package ai.subut.kurjun.metadata.storage.sql;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


/**
 * This class provides connections to database. Connection properties should be properly set in {@code db.properties}
 * file. Data source class name specified in that properties file shall have a corresponding dependency in
 * {@code pom.xml} file.
 *
 */
public class ConnectionFactory
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ConnectionFactory.class );

    String DB_CONNECTION_PROPERTIES = "db.properties";

    private DataSource dataSource;


    private static class ConnectionFactoryHolder
    {
        private static final ConnectionFactory INSTANCE = new ConnectionFactory();
    }


    private ConnectionFactory()
    {
        Properties properties = new Properties();
        try
        {
            properties.load( ClassLoader.getSystemResourceAsStream( DB_CONNECTION_PROPERTIES ) );
        }
        catch ( IOException ex )
        {
            LOGGER.error( "Failed to load DB properties", ex );
            return;
        }

        HikariConfig config = new HikariConfig( properties );
        dataSource = new HikariDataSource( config );
    }


    public static ConnectionFactory getInstance()
    {
        return ConnectionFactoryHolder.INSTANCE;
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

