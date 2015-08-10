package ai.subut.kurjun.repo;


import java.net.URL;

import ai.subut.kurjun.model.repository.Protocol;
import ai.subut.kurjun.model.repository.Repository;


abstract class RepositoryBase implements Repository
{

    protected URL url;


    @Override
    public URL getUrl()
    {
        return url;
    }


    @Override
    public String getPath()
    {
        return url.getPath();
    }


    @Override
    public String getHostname()
    {
        return url.getHost();
    }


    @Override
    public int getPort()
    {
        return url.getPort();
    }


    @Override
    public boolean isSecure()
    {
        return getProtocol().isSecure();
    }


    @Override
    public Protocol getProtocol()
    {
        String protocol = url.getProtocol();
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
    public boolean isKurjun()
    {
        return true;
    }


    @Override
    public String toString()
    {
        return "Kurjun repo: " + url;
    }


}

