package ai.subut.kurjun.security.manager.utils.ssl;


import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSLManager
{
	private static final Logger LOGGER = LoggerFactory.getLogger( SSLManager.class );

	private KeyStore     keyStore = null;
	private KeyStore     trustStore = null;;


	public SSLManager(KeyStore keyStore,KeyStore trustStore)
	{
		this.keyStore   = keyStore;
		this.trustStore = trustStore;
	}

	
	public KeyManager[] getClientKeyManagers(String keyStorePassword)
	{

		KeyManager[] keyManagers = null;
		KeyManagerFactory keyManagerFactory = null;
        
		try
        {
	        keyManagerFactory = KeyManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
			keyManagerFactory.init(keyStore,keyStorePassword.toCharArray());
			keyManagers = keyManagerFactory.getKeyManagers();
        }
        catch ( Exception e )
        {
			LOGGER.error( "Error getting array of client key managers" );
        }
		
		return keyManagers;
	}


	public TrustManager[] getClientTrustManagers(String keyStorePassword)
	{
		TrustManager[] trustManagers = null;
		TrustManagerFactory trustManagerFactory = null;
        
		try
        {
			trustManagerFactory  = TrustManagerFactory.getInstance( TrustManagerFactory.getDefaultAlgorithm() );
			trustManagerFactory.init(trustStore );
			trustManagers = trustManagerFactory.getTrustManagers();
        }
        catch ( Exception e )
        {
			LOGGER.error( "Error getting array of trust managers" );
        }
		
		return trustManagers;
	}


	public TrustManager[] getClientFullTrustManagers()
	{
		return new TrustManager[] {new NaiveTrustManager()};
	}
}
