package ai.subut.kurjun.metadata.storage.nosql;


import java.io.IOException;

import com.datastax.driver.core.Session;


/**
 * This class initializes the schema for the meta data store. Each schema can have a tag that corresponds to specific
 * table in a schema.
 *
 */
public class SchemaInfo
{
    public static final String KEYSPACE = "kurjun_metadata";

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
        if ( tag != null && !tag.isEmpty() )
        {
            return "metadata_" + tag;
        }
        else
        {
            return "metadata";
        }
    }


    /**
     * Prepares the schema for meta data storage. Basically it creates necessary tables for supplied tag.
     *
     * @param session db connection session
     * @throws IOException
     */
    public void createSchema( Session session ) throws IOException
    {
        session.execute( getCreateTableStatement() );
    }


    private String getCreateTableStatement()
    {
        return String.format( "CREATE TABLE IF NOT EXISTS %s.%s (%s text PRIMARY KEY, %s text);",
                              KEYSPACE, getTableName(), CHECKSUM_COLUMN, METADATA_COLUMN );
    }


}

