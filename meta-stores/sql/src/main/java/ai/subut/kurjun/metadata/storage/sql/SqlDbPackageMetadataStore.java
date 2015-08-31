package ai.subut.kurjun.metadata.storage.sql;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.codec.binary.Hex;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.metadata.common.DefaultPackageMetadata;
import ai.subut.kurjun.metadata.common.PackageMetadataListingImpl;
import ai.subut.kurjun.model.metadata.PackageMetadata;
import ai.subut.kurjun.model.metadata.PackageMetadataListing;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;


/**
 * SQL DB implementation of PackageMetadataStore. Refer to {@link ConnectionFactory} for connection details and
 * {@link SqlStatements} for table structure.
 *
 */
class SqlDbPackageMetadataStore implements PackageMetadataStore
{
    @Inject
    Gson gson;

    int batchSize = 1000;


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
        Properties cp = properties.getContextProperties( context );
        ConnectionFactory.getInstance().init( cp );
    }


    /**
     * Constructs a metadata store backed by a SQL DB whose properties are provided by an {@link Properties} instance
     *
     * @param properties
     */
    public SqlDbPackageMetadataStore( Properties properties )
    {
        ConnectionFactory.getInstance().init( properties );
    }


    @Override
    public boolean contains( byte[] md5 ) throws IOException
    {
        Objects.requireNonNull( md5, "Checksum" );
        try ( Connection conn = ConnectionFactory.getInstance().getConnection() )
        {
            PreparedStatement ps = conn.prepareStatement( SqlStatements.SELECT_COUNT );
            ps.setString( 1, Hex.encodeHexString( md5 ) );
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt( 1 ) > 0;
        }
        catch ( SQLException ex )
        {
            throw makeIOException( ex );
        }
    }


    @Override
    public PackageMetadata get( byte[] md5 ) throws IOException
    {
        Objects.requireNonNull( md5, "Checksum" );
        try ( Connection conn = ConnectionFactory.getInstance().getConnection() )
        {
            PreparedStatement ps = conn.prepareStatement( SqlStatements.SELECT_DATA );
            ps.setString( 1, Hex.encodeHexString( md5 ) );
            ResultSet rs = ps.executeQuery();
            if ( rs.next() )
            {
                return gson.fromJson( rs.getString( 1 ), DefaultPackageMetadata.class );
            }
            return null;
        }
        catch ( SQLException ex )
        {
            throw makeIOException( ex );
        }
    }


    @Override
    public boolean put( PackageMetadata meta ) throws IOException
    {
        Objects.requireNonNull( meta, "Package metadata" );
        Objects.requireNonNull( meta.getMd5Sum(), "Checksum of metadata" );
        try ( Connection conn = ConnectionFactory.getInstance().getConnection() )
        {
            // first check if data for checksum already exists
            PreparedStatement ps = conn.prepareStatement( SqlStatements.SELECT_COUNT );
            ps.setString( 1, Hex.encodeHexString( meta.getMd5Sum() ) );
            ResultSet rs = ps.executeQuery();
            if ( rs.next() && rs.getInt( 1 ) == 0 )
            {
                ps = conn.prepareStatement( SqlStatements.INSERT );
                ps.setString( 1, Hex.encodeHexString( meta.getMd5Sum() ) );
                ps.setString( 2, gson.toJson( meta ) );
                return ps.executeUpdate() > 0;
            }
            return false;
        }
        catch ( SQLException ex )
        {
            throw makeIOException( ex );
        }
    }


    @Override
    public boolean remove( byte[] md5 ) throws IOException
    {
        Objects.requireNonNull( md5, "Checksum" );
        try ( Connection conn = ConnectionFactory.getInstance().getConnection() )
        {
            PreparedStatement ps = conn.prepareStatement( SqlStatements.DELETE );
            ps.setString( 1, Hex.encodeHexString( md5 ) );
            return ps.executeUpdate() > 0;
        }
        catch ( SQLException ex )
        {
            throw makeIOException( ex );
        }
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


    private PackageMetadataListing listPackageMetadata( String marker ) throws IOException
    {
        PackageMetadataListingImpl pml = new PackageMetadataListingImpl();
        try ( Connection conn = ConnectionFactory.getInstance().getConnection() )
        {
            PreparedStatement ps;
            if ( marker != null && !marker.isEmpty() )
            {
                ps = conn.prepareStatement( SqlStatements.SELECT_NEXT_ORDERED );
                ps.setString( 1, marker );
            }
            else
            {
                ps = conn.prepareStatement( SqlStatements.SELECT_ORDERED );
            }

            ResultSet rs = ps.executeQuery();
            while ( rs.next() && pml.getPackageMetadata().size() < batchSize )
            {
                DefaultPackageMetadata meta = gson.fromJson( rs.getString( 1 ), DefaultPackageMetadata.class );
                pml.getPackageMetadata().add( meta );
                pml.setMarker( Hex.encodeHexString( meta.getMd5Sum() ) );
            }
            pml.setTruncated( rs.next() );
        }
        catch ( SQLException ex )
        {
            throw makeIOException( ex );
        }
        return pml;
    }


    private IOException makeIOException( SQLException ex )
    {
        return new IOException( "Failed to connect/query db", ex );
    }

}

