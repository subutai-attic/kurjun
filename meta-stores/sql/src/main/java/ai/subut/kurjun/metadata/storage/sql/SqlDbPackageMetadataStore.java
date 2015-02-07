package ai.subut.kurjun.metadata.storage.sql;


import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.codec.binary.Hex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;

import ai.subut.kurjun.metadata.common.DependencyImpl;
import ai.subut.kurjun.metadata.common.PackageMetadataImpl;
import ai.subut.kurjun.model.metadata.Dependency;
import ai.subut.kurjun.model.metadata.PackageMetadata;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;


/**
 * SQL DB implementation of PackageMetadataStore. Refer to {@link ConnectionFactory} for connection details and
 * {@link SqlStatements} for table structure.
 *
 */
public class SqlDbPackageMetadataStore implements PackageMetadataStore
{
    private static final Gson GSON;


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
                return GSON.fromJson( rs.getString( 1 ), PackageMetadataImpl.class );
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
                ps.setString( 2, GSON.toJson( meta ) );
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


    private IOException makeIOException( SQLException ex )
    {
        return new IOException( "Failed to connect/query db", ex );
    }

}

