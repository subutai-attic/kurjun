package ai.subut.kurjun.metadata.storage.sql;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.codec.DecoderException;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.common.utils.PropertyUtils;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.MetadataListingImpl;
import ai.subut.kurjun.model.metadata.MetadataListing;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


/**
 * SQL DB implementation of PackageMetadataStore. Refer to {@link ConnectionFactory} for connection details and {@link
 * SqlStatements} for table structure.
 */

class SqlDbPackageMetadataStore implements PackageMetadataStore
{

    int batchSize = 1000;

    private final KurjunContext context;


    /**
     * Constructs a metadata store backed by a SQL DB whose properties are provided by an {@link Properties} instance of
     * the context.
     *
     * @param properties properties
     * @param context context for which SQL db store is created
     */
    @Inject
    public SqlDbPackageMetadataStore( KurjunProperties properties, @Assisted KurjunContext context )
    {
        ConnectionFactory.getInstance().init( properties );
        this.context = context;
    }


    /**
     * Constructs a metadata store backed by a SQL DB whose properties are provided by an {@link Properties} instance
     */
    public SqlDbPackageMetadataStore( Properties properties )
    {
        this( PropertyUtils.makeKurjunProperties( properties ), new KurjunContext( "" ) );
    }


    @Override
    public boolean contains( Object id ) throws IOException
    {
        Objects.requireNonNull( id, "Id for contains method" );
        try (
                Connection conn = ConnectionFactory.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement( SqlStatements.SELECT_COUNT ) )
        {

            ps.setString( 1, context.getName() );
            ps.setString( 2, String.valueOf( id ) );
            ResultSet rs = ps.executeQuery();

            return rs.next() && rs.getInt( 1 ) > 0;
        }
        catch ( SQLException ex )
        {
            throw makeIOException( ex );
        }
    }


    @Override
    public SerializableMetadata get( Object id ) throws IOException
    {
        Objects.requireNonNull( id, "Id for get method" );
        try ( Connection conn = ConnectionFactory.getInstance().getConnection();
              PreparedStatement ps = conn.prepareStatement( SqlStatements.SELECT_DATA ) )
        {
            ps.setString( 1, context.getName() );
            ps.setString( 2, String.valueOf( id ) );
            ResultSet rs = ps.executeQuery();
            if ( rs.next() )
            {
                return makeMetadata( rs );
            }
        }

        catch ( SQLException | DecoderException ex )
        {
            throw makeIOException( ex );
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

        List<SerializableMetadata> result = new LinkedList<>();
        try ( Connection conn = ConnectionFactory.getInstance().getConnection();
              PreparedStatement ps = conn.prepareStatement( SqlStatements.SELECT_BY_NAME ) )
        {

            ps.setString( 1, context.getName() );
            ps.setString( 2, name );

            ResultSet rs = ps.executeQuery();

            while ( rs.next() )
            {
                result.add( makeMetadata( rs ) );
            }
        }

        catch ( SQLException | DecoderException ex )
        {
            throw makeIOException( ex );
        }
        return result;
    }


    @Override
    public boolean put( SerializableMetadata meta ) throws IOException
    {
        Objects.requireNonNull( meta, "Package metadata" );
        Objects.requireNonNull( meta.getId(), "Id of metadata" );

        if ( contains( meta.getId() ) )
        {
            return false;
        }
        try ( Connection conn = ConnectionFactory.getInstance().getConnection();
              PreparedStatement ps = conn.prepareStatement( SqlStatements.INSERT ) )
        {

            ps.setString( 1, context.getName() );
            ps.setString( 2, String.valueOf( meta.getId() ) );
            ps.setString( 3, meta.getName() );
            ps.setString( 4, meta.getVersion() );
            ps.setString( 5, meta.serialize() );

            return ps.executeUpdate() > 0;
        }
        catch ( SQLException ex )
        {
            throw makeIOException( ex );
        }
    }


    @Override
    public boolean remove( Object id ) throws IOException
    {
        Objects.requireNonNull( id, "Id for remove method" );

        try ( Connection conn = ConnectionFactory.getInstance().getConnection();
              PreparedStatement ps = conn.prepareStatement( SqlStatements.DELETE ) )
        {
            ps.setString( 1, context.getName() );
            ps.setString( 2, String.valueOf( id ) );
            return ps.executeUpdate() > 0;
        }
        catch ( SQLException ex )
        {
            throw makeIOException( ex );
        }
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


    private PreparedStatement getPreparedStatement( String marker, Connection conn ) throws SQLException
    {
        if ( marker != null && !marker.isEmpty() )
        {
            try ( PreparedStatement ps = conn.prepareStatement( SqlStatements.SELECT_NEXT_ORDERED ) )
            {
                ps.setString( 1, context.getName() );
                ps.setString( 2, marker );
                return ps;
            }
        }
        else
        {
            try ( PreparedStatement ps = conn.prepareStatement( SqlStatements.SELECT_ORDERED ) )
            {
                ps.setString( 1, context.getName() );
                return ps;
            }
        }
    }


    private MetadataListing listPackageMetadata( String marker ) throws IOException
    {
        MetadataListingImpl pml = new MetadataListingImpl();

        try ( Connection conn = ConnectionFactory.getInstance().getConnection() )
        {
            PreparedStatement ps = getPreparedStatement( marker, conn );

            ps.setFetchSize( batchSize + 1 );
            ResultSet rs = ps.executeQuery();
            while ( rs.next() )
            {
                if ( pml.getPackageMetadata().size() < batchSize )
                {
                    String md5hex = rs.getString( SqlStatements.CHECKSUM_COLUMN );
                    DefaultMetadata meta = makeMetadata( rs );
                    pml.getPackageMetadata().add( meta );
                    pml.setMarker( md5hex );
                }
                else
                {
                    pml.setTruncated( true );
                    break;
                }
            }
        }
        catch ( SQLException | DecoderException ex )
        {
            throw makeIOException( ex );
        }
        return pml;
    }


    private DefaultMetadata makeMetadata( ResultSet current ) throws SQLException, DecoderException
    {
        String md5hex = current.getString( SqlStatements.CHECKSUM_COLUMN );
        DefaultMetadata meta = new DefaultMetadata();
        meta.setMd5sum( md5hex );
        meta.setName( current.getString( SqlStatements.NAME_COLUMN ) );
        meta.setVersion( current.getString( SqlStatements.VERSION_COLUMN ) );
        meta.setSerialized( current.getString( SqlStatements.DATA_COLUMN ) );
        return meta;
    }


    private IOException makeIOException( Exception ex )
    {
        return new IOException( "Failed to query db", ex );
    }
}

