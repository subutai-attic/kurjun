package ai.subut.kurjun.security.manager.utils.ssl;


import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;


/**
 *
 */
public class RestUtils
{
    public static final long DEFAULT_RECEIVE_TIMEOUT = 1000 * 60 * 10;
    public static final long DEFAULT_CONNECTION_TIMEOUT = 1000 * 15;
    public static final int DEFAULT_MAX_RETRANSMITS = 3;


    //***************************************************
    public enum RequestType
    {
        GET, DELETE, POST
    }


    //***************************************************
    public TrustManager[] getClientTrustManagers()
    {
        TrustManager[] trustManagers = null;
        TrustManagerFactory trustManagerFactory = null;

        try
        {
            //trustManagerFactory  = TrustManagerFactory.getInstance( TrustManagerFactory.getDefaultAlgorithm() );
            //trustManagerFactory.init(trustStore );
            //trustManagers = trustManagerFactory.getTrustManagers();
            //trustStoreData.getPassword();
        }
        catch ( Exception e )
        {
        }

        return trustManagers;
    }


    public TrustManager[] getClientFullTrustManagers()
    {
        return new TrustManager[] { new NaiveTrustManager() };
    }


    //***************************************************
    public static WebClient createWebClient( String url, long connectTimeout, long receiveTimeout, int maxRetries )
    {
        WebClient client = WebClient.create( url );

        HTTPConduit httpConduit = ( HTTPConduit ) WebClient.getConfig( client ).getConduit();

        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout( connectTimeout );
        httpClientPolicy.setReceiveTimeout( receiveTimeout );
        httpClientPolicy.setMaxRetransmits( maxRetries );

        httpConduit.setClient( httpClientPolicy );
        return client;
    }


    //***************************************************
    public static WebClient createTrustedWebClient( String url )
    {
        WebClient client = WebClient.create( url );

        HTTPConduit httpConduit = ( HTTPConduit ) WebClient.getConfig( client ).getConduit();

        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout( DEFAULT_CONNECTION_TIMEOUT );
        httpClientPolicy.setReceiveTimeout( DEFAULT_RECEIVE_TIMEOUT );
        httpClientPolicy.setMaxRetransmits( DEFAULT_MAX_RETRANSMITS );


        httpConduit.setClient( httpClientPolicy );

        SSLManager sslManager = new SSLManager( null, null );

        TLSClientParameters tlsClientParameters = new TLSClientParameters();
        tlsClientParameters.setDisableCNCheck( true );
        tlsClientParameters.setTrustManagers( sslManager.getClientFullTrustManagers() );
        httpConduit.setTlsClientParameters( tlsClientParameters );

        return client;
    }


    //***************************************************
    public static void closeClient( WebClient webClient )
    {
        if ( webClient != null )
        {
            try
            {
                webClient.close();
            }
            catch ( Exception e )
            {
            }
        }
    }
}
