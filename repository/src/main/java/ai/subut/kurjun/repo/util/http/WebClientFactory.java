package ai.subut.kurjun.repo.util.http;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.apache.cxf.jaxrs.client.WebClient;

import ai.subut.kurjun.model.repository.RemoteRepository;


/**
 * Factory of web-client instances to be used to make HTTP requests to remote repositories. This factory gives
 * possibility to customize web clients according to needs of the host environment. Internally, Kurjun is supposed to
 * use only web clients created by this factory, and use those web clients so that HTTP requests to remote repositories
 * are in accordance with needs of the host environment.
 * <p>
 * Use of {@link WebClient} for HTTP requests is only historical; the first environment Kurjun was integrated was
 * Subutai Social where cxf libraries are used (http://cxf.apache.org/).
 * <p>
 * This factory has a basic implementation which produces plain web client instances, i.e. without any custom setups
 * like certificates, SSL, etc.
 *
 * @see DefaultWebClientFactory
 *
 */
public interface WebClientFactory
{

    /**
     * Makes a web client to supplied remote repository with supplied path appended to repository URL and query
     * parameters added.
     *
     * @param remoteRepository remote repository to make web client to
     * @param path path to be appended, may be {@code null}
     * @param queryParams query parameters to be added; may be {@code null}
     * @return
     */
    WebClient make( RemoteRepository remoteRepository, String path, Map<String, String> queryParams );


    /**
     * Builds a URL to the supplied remote repository with supplied path and query parameters.
     *
     * @param remoteRepository remote repository to build URL for
     * @param path additional path of the URL
     * @param queryParams map of query parameters
     * @return
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    static URL buildUrl( RemoteRepository remoteRepository, String path, Map<String, String> queryParams )
            throws URISyntaxException, MalformedURLException
    {
        // merge repository path and supplied path
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append( remoteRepository.getPath() );
        if ( path != null )
        {
            if ( !path.startsWith( "/" ) )
            {
                pathBuilder.append( "/" );
            }
            pathBuilder.append( path );
        }

        // prepare query part of URL
        StringBuilder queryBuilder = new StringBuilder();
        if ( queryParams != null )
        {
            for ( Map.Entry< String, String> e : queryParams.entrySet() )
            {
                if ( queryBuilder.length() > 0 )
                {
                    queryBuilder.append( "&" );
                }
                queryBuilder.append( e.getKey() ).append( "=" ).append( e.getValue() );
            }
        }

        URI uri = new URI( remoteRepository.getProtocol().toString(), null,
                           remoteRepository.getHostname(),
                           remoteRepository.getPort(),
                           pathBuilder.toString(),
                           queryBuilder.toString(),
                           null );

        return uri.toURL();
    }

}

