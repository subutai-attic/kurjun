package ai.subut.kurjun.metadata.storage.nosql;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Iterator;

import org.apache.commons.codec.binary.Hex;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;

import ai.subut.kurjun.metadata.common.DependencyImpl;
import ai.subut.kurjun.metadata.common.PackageMetadataImpl;
import ai.subut.kurjun.metadata.common.PackageMetadataListingImpl;
import ai.subut.kurjun.model.metadata.Dependency;
import ai.subut.kurjun.model.metadata.PackageMetadata;
import ai.subut.kurjun.model.metadata.PackageMetadataListing;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;

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
public class NoSqlPackageMetadataStore implements PackageMetadataStore
{

    private static final Gson GSON;

    int batchSize = 1000;


    static
    {
        GsonBuilder gb = new GsonBuilder().setPrettyPrinting();
        InstanceCreator<Dependency> depInstanceCreator = new InstanceCreator<Dependency>()
        {
            @Override
            public Dependency createInstance( Type type )
            {
                return new DependencyImpl();
            }
        };
        gb.registerTypeAdapter( Dependency.class, depInstanceCreator );

        GSON = gb.create();
    }


    /**
     * Constructor of Cassandra backed package metadata store.
     *
     * @param node the address of the node to connect to; any node of the cluster may be used
     * @param port the port to use; 0 for default
     *
     * @throws IOException
     */
    public NoSqlPackageMetadataStore( String node, int port ) throws IOException
    {
        this( node, port, null );
    }


    /**
     * Constructor of Cassandra backed package metadata store.
     *
     * @param node the address of the node to connect to; any node of the cluster may be used
     * @param port the port to use; 0 for default
     * @param replicationConfig the file to read replication configuration from, useful when there is no keyspace
     * created yet
     *
     * @throws IOException
     */
    public NoSqlPackageMetadataStore( String node, int port, File replicationConfig ) throws IOException
    {
        if ( replicationConfig != null )
        {
            CassandraConnector.getInstance().setReplicationConfigFile( replicationConfig );
        }
        CassandraConnector.getInstance().init( node, port );
    }


    @Override
    public boolean contains( byte[] md5 ) throws IOException
    {
        return get( md5 ) != null;
    }


    @Override
    public PackageMetadata get( byte[] md5 ) throws IOException
    {
        Statement st = QueryBuilder.select().from( SchemaInfo.KEYSPACE, SchemaInfo.TABLE )
                .where( QueryBuilder.eq( SchemaInfo.CHECKSUM_COLUMN, Hex.encodeHexString( md5 ) ) );
        Session session = CassandraConnector.getInstance().getSession();
        ResultSet rs = session.execute( st );
        Row row = rs.one();
        if ( row != null )
        {
            return GSON.fromJson( row.getString( SchemaInfo.METADATA_COLUMN ), PackageMetadataImpl.class );
        }
        return null;
    }


    @Override
    public boolean put( PackageMetadata meta ) throws IOException
    {
        if ( !contains( meta.getMd5Sum() ) )
        {
            Statement st = QueryBuilder.insertInto( SchemaInfo.KEYSPACE, SchemaInfo.TABLE )
                    .value( SchemaInfo.CHECKSUM_COLUMN, Hex.encodeHexString( meta.getMd5Sum() ) )
                    .value( SchemaInfo.METADATA_COLUMN, GSON.toJson( meta ) );
            Session session = CassandraConnector.getInstance().getSession();
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
            Statement st = QueryBuilder.delete().from( SchemaInfo.KEYSPACE, SchemaInfo.TABLE )
                    .where( QueryBuilder.eq( SchemaInfo.CHECKSUM_COLUMN, Hex.encodeHexString( md5 ) ) );
            Session session = CassandraConnector.getInstance().getSession();
            session.execute( st );
            return true;
        }
        return false;
    }


    @Override
    public PackageMetadataListing list() throws IOException
    {
        return listPackageMetadata( null );
    }


    @Override
    public PackageMetadataListing listNextBatch( PackageMetadataListing listing ) throws IOException
    {
        if ( listing.isTruncated() && listing.getMarker() != null )
        {
            return listPackageMetadata( listing.getMarker().toString() );
        }
        throw new IllegalStateException( "Listing is not truncated or no marker specified" );
    }


    private PackageMetadataListing listPackageMetadata( String marker )
    {
        PackageMetadataListingImpl res = new PackageMetadataListingImpl();

        Statement st = QueryBuilder.select().from( SchemaInfo.KEYSPACE, SchemaInfo.TABLE )
                .where( gt( token( SchemaInfo.CHECKSUM_COLUMN ), fcall( "token", marker != null ? marker : "" ) ) )
                .limit( batchSize + 1 );
        // (*) limit with one more item to detect whether there are more results to fetch

        Session session = CassandraConnector.getInstance().getSession();
        ResultSet rs = session.execute( st );

        Iterator<Row> it = rs.iterator();
        while ( it.hasNext() && res.getPackageMetadata().size() < batchSize )
        {
            Row row = it.next();
            String json = row.getString( SchemaInfo.METADATA_COLUMN );
            res.getPackageMetadata().add( GSON.fromJson( json, PackageMetadataImpl.class ) );
            res.setMarker( row.getString( SchemaInfo.CHECKSUM_COLUMN ) );
        }
        res.setTruncated( it.hasNext() );
        return res;
    }

}

