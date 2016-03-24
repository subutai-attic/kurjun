package ai.subut.kurjun.repo.util.http;


import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import ai.subut.kurjun.model.repository.RemoteRepository;
import ai.subut.kurjun.security.manager.utils.ssl.RestUtils;


/**
 * Default implementation of {@link WebClientFactory}. This produces plain web clients without any custom settings.
 */
class DefaultWebClientFactory implements WebClientFactory
{

    static final long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis( 5 );


    @Override
    public WebClient make( RemoteRepository remoteRepository, String path, Map<String, String> queryParams )
    {
        try
        {
            URL url = WebClientFactory.buildUrl( remoteRepository, path, queryParams );

            WebClient webClient = WebClient.create( url.toString() );
            HTTPConduit httpConduit = ( HTTPConduit ) WebClient.getConfig( webClient ).getConduit();

            HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
            httpClientPolicy.setConnectionTimeout( CONNECTION_TIMEOUT );

            httpConduit.setClient( httpClientPolicy );

            return webClient;
        }
        catch ( URISyntaxException | MalformedURLException ex )
        {
            throw new IllegalArgumentException( "Failed to build URL", ex );
        }
    }


    @Override
    public WebClient makeSecure( final RemoteRepository remoteRepository, final String path,
                                 final Map<String, String> queryParams )
    {
        try
        {
            URL url = WebClientFactory.buildUrl( remoteRepository, path, queryParams );

            WebClient webClient = RestUtils.createTrustedWebClient( url.toExternalForm() );

            return webClient;
        }
        catch ( URISyntaxException | MalformedURLException e )
        {
            e.printStackTrace();
        }
        return null;
    }
}

