package ai.subut.kurjun.metadata.storage.nosql;


import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.inject.Inject;
import com.google.inject.ProvisionException;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.MetadataListingImpl;
import ai.subut.kurjun.model.metadata.MetadataListing;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;

import static com.datastax.driver.core.querybuilder.QueryBuilder.fcall;
import static com.datastax.driver.core.querybuilder.QueryBuilder.gt;
import static com.datastax.driver.core.querybuilder.QueryBuilder.token;


/**
 * Cassandra backed implementation of {@link PackageMetadata}. Any one node of the cluster should be specified in the
 * constructor. Keyspace with simple replication factor of 3 and a table in that keyspace are automatically created if
 * not already exist. If keyspace does not exist yet, a replication configuration file can be specified which shall
 * contain replication data in JSON format. Refer to
 * <a href="http://www.datastax.com/documentation/cql/3.0/cql/cql_reference/create_keyspace_r.html">this link</a> for
 * more info about replication configuration.
 *
 */
class NoSqlPackageMetadataStore implements PackageMetadataStore
{

    int batchSize = 1000;
    private Session session;
    private SchemaInfo schemaInfo;


    @Inject
    public NoSqlPackageMetadataStore( CassandraSessionProvider sessionProvider, @Assisted KurjunContext context )
    {
        this.session = sessionProvider.get();

        schemaInfo = new SchemaInfo();
        schemaInfo.setTag( context.getName() );
        try
        {
            schemaInfo.createSchema( session );
        }
        catch ( IOException ex )
        {
            throw new ProvisionException( "Failed to construct metadata store", ex );
        }
    }


    @Override
    public boolean contains( Object id ) throws IOException
    {
        return get( id ) != null;
    }


    @Override
    public SerializableMetadata get( Object id ) throws IOException
    {
        Statement st = QueryBuilder.select().from( SchemaInfo.KEYSPACE, schemaInfo.getTableName() )
                .where( QueryBuilder.eq( SchemaInfo.CHECKSUM_COLUMN, String.valueOf( id ) ) );
        ResultSet rs = session.execute( st );
        Row row = rs.one();
        if ( row != null )
        {
            return makeMetadata( row );
        }
        return null;
    }


    @Override
    public List<SerializableMetadata> get( String name ) throws IOException
    {
        if ( name == null )
        {
            return Collections.emptyList();
        }

        Statement st = QueryBuilder.select().from( SchemaInfo.KEYSPACE, schemaInfo.getTableName() )
                .where( QueryBuilder.eq( SchemaInfo.NAME_COLUMN, name ) );
        ResultSet rs = session.execute( st );

        List<SerializableMetadata> result = new LinkedList<>();
        Iterator<Row> it = rs.iterator();
        while ( it.hasNext() )
        {
            result.add( makeMetadata( it.next() ) );
        }
        return result;
    }


    @Override
    public boolean put( SerializableMetadata meta ) throws IOException
    {
        if ( !contains( meta.getId() ) )
        {
            Statement st = QueryBuilder.insertInto( SchemaInfo.KEYSPACE, schemaInfo.getTableName() )
                    .value( SchemaInfo.CHECKSUM_COLUMN, String.valueOf( meta.getId() ) )
                    .value( SchemaInfo.NAME_COLUMN, meta.getName() )
                    .value( SchemaInfo.VERSION_COLUMN, meta.getVersion() )
                    .value( SchemaInfo.DATA_COLUMN, meta.serialize() );
            session.execute( st );
            return true;
        }
        return false;
    }


    @Override
    public boolean remove( Object id ) throws IOException
    {
        if ( contains( id ) )
        {
            Statement st = QueryBuilder.delete().from( SchemaInfo.KEYSPACE, schemaInfo.getTableName() )
                    .where( QueryBuilder.eq( SchemaInfo.CHECKSUM_COLUMN, String.valueOf( id ) ) );
            session.execute( st );
            return true;
        }
        return false;
    }


    @Override
    public MetadataListing list() throws IOException
    {
        return listPackageMetadata( null );
    }


    @Override
    public MetadataListing listNextBatch( MetadataListing listing ) throws IOException
    {
        if ( listing.isTruncated() && listing.getMarker() != null )
        {
            return listPackageMetadata( listing.getMarker().toString() );
        }
        throw new IllegalStateException( "Listing is not truncated or no marker specified" );
    }


    private MetadataListing listPackageMetadata( String marker ) throws IOException
    {
        MetadataListingImpl res = new MetadataListingImpl();

        Statement st = QueryBuilder.select().from( SchemaInfo.KEYSPACE, schemaInfo.getTableName() )
                .where( gt( token( SchemaInfo.CHECKSUM_COLUMN ), fcall( "token", marker != null ? marker : "" ) ) )
                .limit( batchSize + 1 );
        // (*) limit with one more item to detect whether there are more results to fetch

        ResultSet rs = session.execute( st );

        Iterator<Row> it = rs.iterator();
        while ( it.hasNext() )
        {
            if ( res.getPackageMetadata().size() < batchSize )
            {
                Row row = it.next();
                res.getPackageMetadata().add( makeMetadata( row ) );
                res.setMarker( row.getString( SchemaInfo.CHECKSUM_COLUMN ) );
            }
            else
            {
                res.setTruncated( true );
                break;
            }
        }
        return res;
    }


    private SerializableMetadata makeMetadata( Row row ) throws IOException
    {
        byte[] md5;
        try
        {
            String md5hex = row.getString( SchemaInfo.CHECKSUM_COLUMN );
            md5 = Hex.decodeHex( md5hex.toCharArray() );
        }
        catch ( DecoderException ex )
        {
            throw new IOException( ex );
        }
        DefaultMetadata m = new DefaultMetadata();
        m.setMd5sum( md5 );
        m.setName( row.getString( SchemaInfo.NAME_COLUMN ) );
        m.setVersion( row.getString( SchemaInfo.VERSION_COLUMN ) );
        m.setSerialized( row.getString( SchemaInfo.DATA_COLUMN ) );
        return m;
    }


}

