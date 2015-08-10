package ai.subut.kurjun.repo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    /**
     * Reads releases from {@code conf/distributions} file of this apt repository.
     *
     * @throws IOException on any read failures
     * @return list of release names like 'trusty', 'utopic', etc.
     */
    protected List<String> readAptReleases() throws IOException
    {
        URL distributionsUrl;
        try
        {
            distributionsUrl = url.toURI().resolve( "conf/distributions" ).toURL();
        }
        catch ( URISyntaxException ex )
        {
            throw new IOException( "Repo URL can not be converted to URI", ex );
        }

        Pattern pattern = Pattern.compile( "Codename:\\s*(\\w+)" );

        List<String> releases = new ArrayList<>();
        try ( BufferedReader br = new BufferedReader( new InputStreamReader( distributionsUrl.openStream() ) ) )
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


    /**
     * Makes a URL to a file of a given release of this repository.
     *
     * @param release release name like 'trusty'
     * @param signed indicates if clear-signed release index file should be returned, usually named as InRelease
     * @return
     */
    protected URL makeReleaseIndexUrl( String release, boolean signed )
    {
        String fileName = signed ? "InRelease" : "Release";
        try
        {
            URI uri = url.toURI().resolve( String.format( "dists/%s/%s", release, fileName ) );
            return uri.toURL();
        }
        catch ( URISyntaxException | MalformedURLException ex )
        {
            throw new IllegalStateException( "Repo URL is not strictly formatted according to RFC2396", ex );
        }
    }
}

