package ai.subut.kurjun.metadata.storage.nosql;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Session;


/**
 * This class initializes the schema for the meta data store. Each schema can have a tag that corresponds to specific
 * table in a schema.
 *
 */
class SchemaInfo
{
    public static final String KEYSPACE = "kurjun_metadata";
    public static final String TABLE_PREFIX = "metadata";

    public static final String CHECKSUM_COLUMN = "checksum";
    public static final String METADATA_COLUMN = "metadata";

    private String tag;


    /**
     * Gets tag of the schema.
     *
     * @return schema tag if set; null otherwise
     */
    public String getTag()
    {
        return tag;
    }


    /**
     * Sets the tag for this schema. If schema was created before for the supplied tag then this schema info will refer
     * to that schema created before.
     *
     * @param tag tag for the schema
     */
    public void setTag( String tag )
    {
        this.tag = tag;
    }


    /**
     * Gets table name this schema info us using for meta data storage.
     *
     * @return table name
     */
    public String getTableName()
    {
        if ( tag != null )
        {
            return TABLE_PREFIX + "_" + tag;
        }
        else
        {
            return TABLE_PREFIX;
        }
    }


    /**
     * A wrapper to {@link SchemaInfo#createSchema(com.datastax.driver.core.Session, java.io.File) }.
     *
     * @param session
     * @throws IOException
     */
    public void createSchema( Session session ) throws IOException
    {
        createSchema( session, null );
    }


    /**
     * Prepares the schema for meta data storage. Basically it creates necessary schema(keyspace) and table.
     *
     * @param session db connection session
     * @param replicationConfigFile file to replication configuration that should be used when creating a keyspace;
     * {@code null} can supplied in which case the default replication factor is used.
     * @throws IOException
     */
    public void createSchema( Session session, File replicationConfigFile ) throws IOException
    {
        session.execute( getCreateKeyspaceStatement( replicationConfigFile ) );
        session.execute( getCreateTableStatement() );
    }


    private String getCreateKeyspaceStatement( File replicationConfigFile )
    {
        StringBuilder sb = new StringBuilder();
        try ( BufferedReader br = new BufferedReader( new InputStreamReader(
                getReplicationConfigStream( replicationConfigFile ) ) ) )
        {
            String line;
            while ( ( line = br.readLine() ) != null )
            {
                sb.append( line ).append( " " );
            }
        }
        catch ( IOException ex )
        {
            LoggerFactory.getLogger( SchemaInfo.class ).error( "Failed to read replication config", ex );
            return null;
        }
        return String.format( "CREATE KEYSPACE IF NOT EXISTS %s WITH replication = %s;", KEYSPACE, sb.toString() );
    }


    private InputStream getReplicationConfigStream( File replicationConfigFile ) throws FileNotFoundException
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


    private String getCreateTableStatement()
    {
        return String.format( "CREATE TABLE IF NOT EXISTS %s.%s (%s text PRIMARY KEY, %s text);",
                              KEYSPACE, getTableName(), CHECKSUM_COLUMN, METADATA_COLUMN );
    }


}

