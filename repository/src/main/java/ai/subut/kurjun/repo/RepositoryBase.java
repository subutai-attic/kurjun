package ai.subut.kurjun.repo;


import ai.subut.kurjun.model.repository.Protocol;
import ai.subut.kurjun.model.repository.Repository;


/**
 * Abstract base class for repositories. This can be a base for either local or remote repositories.
 *
 */
abstract class RepositoryBase implements Repository
{

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
        return "Kurjun repository: " + getUrl();
    }


}

