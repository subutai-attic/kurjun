package ai.subut.kurjun.security;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.common.service.KurjunPropertyKey;
import ai.subut.kurjun.security.service.PgpKeyFetcher;
import ai.subut.kurjun.security.utils.PGPUtils;


class PgpKeyFercherImpl implements PgpKeyFetcher
{
    private static final Logger LOGGER = LoggerFactory.getLogger( PgpKeyFercherImpl.class );

    private URL keyserverUrl;


    @Inject
    public PgpKeyFercherImpl( KurjunProperties kurjunProperties ) throws MalformedURLException
    {
        this( kurjunProperties.get( KurjunPropertyKey.SECURITY_KEYSERVER_URL ) );
    }


    public PgpKeyFercherImpl( String url ) throws MalformedURLException
    {
        this.keyserverUrl = new URL( url );

    }


    @Override
    public PGPPublicKey get( String fingerprint )
    {
        try
        {
            URI uri = new URI( keyserverUrl.getProtocol(), null, keyserverUrl.getHost(), keyserverUrl.getPort(),
                               keyserverUrl.getPath() + "/pks/lookup",
                               "op=get&search=0x" + fingerprint,
                               null );
            HttpURLConnection conn = ( HttpURLConnection ) uri.toURL().openConnection();
            if ( conn.getResponseCode() == 200 )
            {
                return PGPUtils.readPGPKey( conn.getInputStream() );
            }
        }
        catch ( IOException | URISyntaxException | PGPException ex )
        {
            LOGGER.error( "Failed to read PGP key from keyserver", ex );
        }
        return null;
    }

}

