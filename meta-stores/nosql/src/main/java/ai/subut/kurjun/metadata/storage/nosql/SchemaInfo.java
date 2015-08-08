package ai.subut.kurjun.metadata.storage.nosql;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.LoggerFactory;


class SchemaInfo
{
    public static final String KEYSPACE = "kurjun_metadata";
    public static final String TABLE = "metadata";

    public static final String CHECKSUM_COLUMN = "checksum";
    public static final String METADATA_COLUMN = "metadata";


    public static String getCreateKeyspaceStatement( InputStream replicationConfigStream )
    {
        StringBuilder sb = new StringBuilder();
        try ( BufferedReader br = new BufferedReader( new InputStreamReader( replicationConfigStream ) ) )
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
        String repl = sb.toString();
        return String.format( "CREATE KEYSPACE IF NOT EXISTS %s WITH replication = %s;", KEYSPACE, sb.toString() );
    }


    public static String getCreateTableStatement()
    {
        return String.format( "CREATE TABLE IF NOT EXISTS %s.%s (%s text PRIMARY KEY, %s text);",
                              KEYSPACE, TABLE, CHECKSUM_COLUMN, METADATA_COLUMN );
    }


}

