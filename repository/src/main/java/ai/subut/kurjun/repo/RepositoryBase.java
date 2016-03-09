package ai.subut.kurjun.repo;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import com.google.inject.Inject;

import ai.subut.kurjun.model.context.ArtifactContext;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.Protocol;
import ai.subut.kurjun.model.repository.Repository;
import ai.subut.kurjun.model.user.UserContext;

import static java.util.UUID.randomUUID;


/**
 * Abstract base class for repositories. This can be a base for either local or remote repositories.
 */
abstract class RepositoryBase implements Repository
{

    private final UUID identifier = randomUUID();

    @Inject
    ArtifactContext artifactContext;


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


    @Override
    public byte[] md5()
    {
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance( "MD5" );
            List<SerializableMetadata> list = listPackages();

            if ( list.size() == 0 )
            {
                messageDigest.update( list.toString().getBytes() );
                return messageDigest.digest();
            }
        }
        catch ( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
        }

        return new byte[0];
    }


    public void index( Metadata metadata, UserContext userContext )
    {
        artifactContext.store( metadata.getMd5Sum(), userContext );
    }


    public void unindex( byte[] md5 )
    {
        artifactContext.remove( md5 );
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
