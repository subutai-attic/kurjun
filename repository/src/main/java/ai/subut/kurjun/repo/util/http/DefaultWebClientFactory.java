package ai.subut.kurjun.repo.util.http;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import ai.subut.kurjun.model.repository.NonLocalRepository;


/**
 * Default implementation of {@link WebClientFactory}. This produces plain web clients without any custom settings.
 *
 */
class DefaultWebClientFactory implements WebClientFactory
{


    @Override
    public WebClient make( NonLocalRepository remoteRepository, String path, Map<String, String> queryParams )
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

        try
        {
            URI uri = new URI( remoteRepository.getProtocol().toString(), null,
                               remoteRepository.getHostname(),
                               remoteRepository.getPort(),
                               pathBuilder.toString(),
                               queryBuilder.toString(),
                               null );

            return makeClient( uri );
        }
        catch ( URISyntaxException | MalformedURLException ex )
        {
            throw new IllegalArgumentException( "Failed to build URL", ex );
        }
    }


    @Override
    public long getConnectionTimeout()
    {
        return TimeUnit.SECONDS.toMillis( 5 );
    }


    private WebClient makeClient( URI uri ) throws MalformedURLException
    {
        WebClient webClient = WebClient.create( uri.toURL().toString() );
        HTTPConduit httpConduit = ( HTTPConduit ) WebClient.getConfig( webClient ).getConduit();

        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout( getConnectionTimeout() );

        httpConduit.setClient( httpClientPolicy );

        return webClient;
    }

}

