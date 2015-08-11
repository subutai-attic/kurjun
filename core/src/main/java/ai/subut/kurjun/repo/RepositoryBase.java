package ai.subut.kurjun.repo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.repository.Protocol;
import ai.subut.kurjun.model.repository.Repository;
import ai.subut.kurjun.repo.http.HttpHandler;
import ai.subut.kurjun.riparser.service.ReleaseIndexParser;


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
    public Set<ReleaseFile> getDistributions()
    {
        Set<ReleaseFile> result = new HashSet<>();
        try
        {
            List<String> releases = readAptReleases();
            for ( String release : releases )
            {
                try ( InputStream is = httpHandler.streamReleaseIndexFile( release, false ) )
                {
                    ReleaseFile rf = getReleaseIndexParser().parse( is );
                    result.add( rf );
                }
            }
        }
        catch ( IOException ex )
        {
            getLogger().error( "Failed to read releases of a remote repo", ex );
        }
        return result;
    }


    @Override
    public String toString()
    {
        return "Kurjun repo: " + url;
    }


    /**
     * Gets a logger instance corresponding to implementation classes of this class.
     *
     * @return
     */
    protected abstract Logger getLogger();


    /**
     * Implementing classes should provide {@link ReleaseIndexParser} instance to be able to parse release index files
     * of the repository.
     *
     * @return
     */
    protected abstract ReleaseIndexParser getReleaseIndexParser();


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

