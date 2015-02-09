package ai.subut.kurjun.metadata.storage.sql;


/**
 * This class reflects expected database table structure. A simple table is expected with two columns holding and
 * checksum of the file corresponding to package metadata and JSON serialized metadata. It is highly recommended that
 * checksum column is indexed.
 *
 */
class SqlStatements
{
    public static final String TABLE_NAME = "metadata";
    public static final String CHECKSUM_COLUMN = "checksum";
    public static final String METADATA_COLUMN = "metadata";

    //
    // SQL statement templates for CRUD operations
    //
    public static final String SELECT_COUNT;
    public static final String SELECT_DATA;
    public static final String INSERT;
    public static final String DELETE;


    static
    {
        SELECT_COUNT = String.format( "SELECT COUNT(*) FROM %s WHERE %s = ?", TABLE_NAME, CHECKSUM_COLUMN );
        SELECT_DATA = String.format( "SELECT %s FROM %s WHERE %s = ?", METADATA_COLUMN, TABLE_NAME, CHECKSUM_COLUMN );
        INSERT = String.format( "INSERT INTO %s(%s,%s) VALUES(?, ?)", TABLE_NAME, CHECKSUM_COLUMN, METADATA_COLUMN );
        DELETE = String.format( "DELETE FROM %s WHERE %s = ?", TABLE_NAME, CHECKSUM_COLUMN );
    }
}

