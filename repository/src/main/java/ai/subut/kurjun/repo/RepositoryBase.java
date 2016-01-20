package ai.subut.kurjun.repo;


import java.util.UUID;

import ai.subut.kurjun.model.repository.Protocol;
import ai.subut.kurjun.model.repository.Repository;

import static java.util.UUID.randomUUID;


/**
 * Abstract base class for repositories. This can be a base for either local or remote repositories.
 *
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
