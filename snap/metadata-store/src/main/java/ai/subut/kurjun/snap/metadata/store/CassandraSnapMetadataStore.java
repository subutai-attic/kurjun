package ai.subut.kurjun.snap.metadata.store;


import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.ProvisionException;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.metadata.storage.nosql.SchemaInfo;
import ai.subut.kurjun.model.metadata.snap.SnapMetadata;
import ai.subut.kurjun.model.metadata.snap.SnapMetadataFilter;
import ai.subut.kurjun.model.metadata.snap.SnapMetadataStore;
import ai.subut.kurjun.snap.DefaultSnapMetadata;


/**
 * Snap meta data store backed by Cassandra.
 *
 */
class CassandraSnapMetadataStore implements SnapMetadataStore
{

    @Inject
    private Gson gson;

    private Session session;
    private final SchemaInfo schemaInfo;


    @Inject
    public CassandraSnapMetadataStore( Session session, @Assisted KurjunContext context )
    {
        this.session = session;

        schemaInfo = new SchemaInfo();
        schemaInfo.setTag( context.getName() );
        try
        {
            schemaInfo.createSchema( session );
        }
        catch ( IOException ex )
        {
            throw new ProvisionException( "Failed to construct snap metadata store", ex );
        }
    }


    @Override
    public boolean contains( byte[] md5 ) throws IOException
    {
        return get( md5 ) != null;
    }


    @Override
    public SnapMetadata get( byte[] md5 ) throws IOException
    {
        Statement st = QueryBuilder.select().from( SchemaInfo.KEYSPACE, schemaInfo.getTableName() )
                .where( QueryBuilder.eq( SchemaInfo.CHECKSUM_COLUMN, Hex.encodeHexString( md5 ) ) );
        ResultSet rs = session.execute( st );
        Row row = rs.one();
        if ( row != null )
        {
            return gson.fromJson( row.getString( SchemaInfo.METADATA_COLUMN ), DefaultSnapMetadata.class );
        }
        return null;
    }


    @Override
    public List<SnapMetadata> list( SnapMetadataFilter filter ) throws IOException
    {
        // TODO: this makes select without filters
        Statement st = QueryBuilder.select().from( SchemaInfo.KEYSPACE, schemaInfo.getTableName() );
        ResultSet rs = session.execute( st );

        List<SnapMetadata> result = new LinkedList<>();

        Iterator<Row> it = rs.iterator();
        while ( it.hasNext() )
        {
            String json = it.next().getString( SchemaInfo.METADATA_COLUMN );
            DefaultSnapMetadata m = gson.fromJson( json, DefaultSnapMetadata.class );
            if ( filter.test( m ) )
            {
                result.add( m );
            }
        }
        return result;
    }


    @Override
    public boolean put( SnapMetadata metadata ) throws IOException
    {
        if ( !contains( metadata.getMd5Sum() ) )
        {
            Statement st = QueryBuilder.insertInto( SchemaInfo.KEYSPACE, schemaInfo.getTableName() )
                    .value( SchemaInfo.CHECKSUM_COLUMN, Hex.encodeHexString( metadata.getMd5Sum() ) )
                    .value( SchemaInfo.METADATA_COLUMN, gson.toJson( metadata ) );
            session.execute( st );
            return true;
        }
        return false;
    }


    @Override
    public boolean remove( byte[] md5 ) throws IOException
    {
        if ( contains( md5 ) )
        {
            Statement st = QueryBuilder.delete().from( SchemaInfo.KEYSPACE, schemaInfo.getTableName() )
                    .where( QueryBuilder.eq( SchemaInfo.CHECKSUM_COLUMN, Hex.encodeHexString( md5 ) ) );
            session.execute( st );
            return true;
        }
        return false;
    }

}

