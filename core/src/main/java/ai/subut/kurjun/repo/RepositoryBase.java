package ai.subut.kurjun.repo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.subut.kurjun.model.repository.Protocol;
import ai.subut.kurjun.model.repository.Repository;
import ai.subut.kurjun.repo.http.HttpHandler;


abstract class RepositoryBase implements Repository
{

    protected URL url;
    protected HttpHandler httpHandler = new HttpHandler( this );


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


    /**
     * Reads releases from {@code conf/distributions} file of this apt repository.
     *
     * @throws IOException on any read failures
     * @return list of release names like 'trusty', 'utopic', etc.
     */
    protected List<String> readAptReleases() throws IOException
    {
        Pattern pattern = Pattern.compile( "Codename:\\s*(\\w+)" );

        List<String> releases = new ArrayList<>();
        try ( BufferedReader br = new BufferedReader( new InputStreamReader( httpHandler.streamDistributionsFile() ) ) )
        {
            String line;
            while ( ( line = br.readLine() ) != null )
            {
                Matcher matcher = pattern.matcher( line );
                if ( matcher.matches() )
                {
                    releases.add( matcher.group( 1 ) );
                }
            }
        }
        return releases;
    }

}

