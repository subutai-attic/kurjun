package ai.subut.kurjun.metadata.storage.sql;


import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;


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
    public static final String NAME_COLUMN = "name";
    public static final String VERSION_COLUMN = "version";
    public static final String DATA_COLUMN = "data";

    //
    // SQL statement templates for CRUD operations
    //
    public static final String SELECT_COUNT;
    public static final String SELECT_DATA;
    public static final String SELECT_ORDERED;
    public static final String SELECT_NEXT_ORDERED;
    public static final String INSERT;
    public static final String DELETE;


    static
    {
        List<String> allColumns = Arrays.asList( CHECKSUM_COLUMN, NAME_COLUMN, VERSION_COLUMN, DATA_COLUMN );
        String all = StringUtils.join( allColumns, ',' );

        SELECT_COUNT = new SQL().SELECT( "COUNT(*)" ).FROM( TABLE_NAME ).WHERE( CHECKSUM_COLUMN + " = ?" ).toString();

        SELECT_DATA = new SQL().SELECT( all ).FROM( TABLE_NAME ).WHERE( CHECKSUM_COLUMN + " = ?" ).toString();

        SELECT_ORDERED = new SQL().SELECT( all ).FROM( TABLE_NAME ).ORDER_BY( CHECKSUM_COLUMN ).toString();

        SELECT_NEXT_ORDERED = new SQL().SELECT( all ).FROM( TABLE_NAME ).WHERE( CHECKSUM_COLUMN + " > ?" )
                .ORDER_BY( CHECKSUM_COLUMN ).toString();

        INSERT = new SQL().INSERT_INTO( TABLE_NAME ).VALUES( CHECKSUM_COLUMN, "?" ).VALUES( NAME_COLUMN, "?" )
                .VALUES( VERSION_COLUMN, "?" ).VALUES( DATA_COLUMN, "?" )
                .toString();

        DELETE = new SQL().DELETE_FROM( TABLE_NAME ).WHERE( CHECKSUM_COLUMN + " = ?" ).toString();

    }
}

