package ai.subut.kurjun.repo.util.http;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import ai.subut.kurjun.model.repository.RemoteRepository;
import ai.subut.kurjun.security.manager.utils.ssl.NaiveTrustManager;
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
            throw new IllegalArgumentException( "Failed to create webclient", e );
        }
    }

    @Override
    public URLConnection openSecureConnection( final RemoteRepository remoteRepository, final String path,
                                               final Map<String, String> queryParams )
    {
        try
        {
            URL url = WebClientFactory.buildUrl( remoteRepository, path, queryParams );
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout( ( int ) RestUtils.DEFAULT_CONNECTION_TIMEOUT );
            conn.setReadTimeout( ( int ) RestUtils.DEFAULT_RECEIVE_TIMEOUT );
            if ( conn instanceof HttpsURLConnection )
            {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, new TrustManager[] { new NaiveTrustManager() },
                        new java.security.SecureRandom());
                (( HttpsURLConnection )conn).setSSLSocketFactory(sc.getSocketFactory());
            }
            conn.connect();
            return conn;
        }
        catch ( IOException | NoSuchAlgorithmException | URISyntaxException | KeyManagementException e )
        {
            throw new IllegalArgumentException( "Failed to create webclient", e );
        }
    }
}

