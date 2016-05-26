package ai.subut.kurjun.repo;


import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.UUID;

import org.apache.commons.io.output.ByteArrayOutputStream;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.repository.Protocol;
import ai.subut.kurjun.model.repository.Repository;

import static java.util.UUID.randomUUID;


/**
 * Abstract base class for repositories. This can be a base for either local or remote repositories.
 */
abstract class RepositoryBase implements Repository
{

    private final UUID identifier = randomUUID();

    @Override
    public UUID getIdentifier()
    {
        return identifier;
    }


    @Override
    public String getPath()
    {
        return getUrl().getPath();
    }


    @Override
    public String getHostname()
    {
        return getUrl().getHost();
    }


    @Override
    public int getPort()
    {
        return getUrl().getPort();
    }


    @Override
    public boolean isSecure()
    {
        return getProtocol().isSecure();
    }

    public abstract KurjunContext getContext();

    @Override
    public Protocol getProtocol()
    {
        String protocol = getUrl().getProtocol();
        for ( Protocol p : Protocol.values() )
        {
            if ( protocol.equalsIgnoreCase( p.toString() ) )
            {
                return p;
            }
        }
        throw new IllegalStateException( "Unsupported protocol: " + protocol );
    }

    public ByteArrayOutputStream getPackageStream( InputStream is, PackageProgressListener progressListener ) throws IOException
    {
        ByteBuffer byteBuffer = ByteBuffer.allocate( 8192 );
        ReadableByteChannel rbc = Channels.newChannel( is );
        int bytesRead = rbc.read( byteBuffer );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        WritableByteChannel wbc = Channels.newChannel( baos );

        while ( bytesRead > 0 )
        {
            //limit is set to current position and position is set to zero
            byteBuffer.flip();
            ByteBuffer duplicate = byteBuffer.duplicate();
            while ( duplicate.hasRemaining() )
            {
                wbc.write( duplicate );
            }
            if ( progressListener != null )
            {
                progressListener.writeBytes( byteBuffer );
            }
            byteBuffer.clear();
            bytesRead = rbc.read( byteBuffer );
        }
        return baos;
    }


    @Override
    public String toString()
    {
        try
        {
            return "Kurjun repository: " + getUrl();
        }
        // TODO: clear try-catch after resolving getUrl for LocalRepositories
        catch ( UnsupportedOperationException e )
        {
            return "Kurjun repository: Local Repo";
        }
    }
}
