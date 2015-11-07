package ai.subut.kurjun.repo;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.metadata.common.snap.DefaultSnapMetadata;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.NonLocalRepository;
import ai.subut.kurjun.model.security.Identity;
import ai.subut.kurjun.repo.util.SecureRequestFactory;


/**
 * Non-local snap repository implementation.
 *
 */
class NonLocalSnapRepository extends RepositoryBase implements NonLocalRepository
{

    private static final Logger LOGGER = LoggerFactory.getLogger( NonLocalSnapRepository.class );

    static final String INFO_PATH = "info";
    static final String GET_PATH = "get";

    @Inject
    private Gson gson;

    private final URL url;
    private final Identity identity;


    @Inject
    public NonLocalSnapRepository( @Assisted String url, @Assisted Identity identity )
    {
        this.identity = identity;
        try
        {
            this.url = new URL( url );
        }
        catch ( MalformedURLException ex )
        {
            throw new IllegalArgumentException( "Invalid url", ex );
        }
    }


    @Override
    public Identity getIdentity()
    {
        return identity;
    }


    @Override
    public URL getUrl()
    {
        return url;
    }


    @Override
    public boolean isKurjun()
    {
        return true;
    }


    @Override
    public Set<ReleaseFile> getDistributions()
    {
        throw new UnsupportedOperationException( "Not supported in snap repositories." );
    }


    @Override
    public SerializableMetadata getPackageInfo( Metadata metadata )
    {
        SecureRequestFactory secreq = new SecureRequestFactory( this );
        WebClient webClient = secreq.makeClient( INFO_PATH, makeParamsMap( metadata ) );
        if ( identity != null )
        {
            webClient.header( KurjunConstants.HTTP_HEADER_FINGERPRINT, identity.getKeyFingerprint() );
        }

        Response resp = webClient.get();
        if ( resp.getStatus() == Response.Status.OK.getStatusCode() )
        {
            if ( resp.getEntity() instanceof InputStream )
            {
                try
                {
                    String json = IOUtils.toString( ( InputStream ) resp.getEntity() );
                    return gson.fromJson( json, DefaultSnapMetadata.class );
                }
                catch ( IOException ex )
                {
                    LOGGER.error( "Failed to read response data", ex );
                }
            }
        }
        return null;
    }


    @Override
    public InputStream getPackageStream( Metadata metadata )
    {
        SecureRequestFactory secreq = new SecureRequestFactory( this );
        WebClient webClient = secreq.makeClient( GET_PATH, makeParamsMap( metadata ) );
        if ( identity != null )
        {
            webClient.header( KurjunConstants.HTTP_HEADER_FINGERPRINT, identity.getKeyFingerprint() );
        }

        Response resp = webClient.get();
        if ( resp.getStatus() == Response.Status.OK.getStatusCode() )
        {
            if ( resp.getEntity() instanceof InputStream )
            {
                return ( InputStream ) resp.getEntity();
            }
        }
        return null;
    }


    private Map< String, String> makeParamsMap( Metadata metadata )
    {
        Map<String, String> params = new HashMap<>();
        if ( metadata.getMd5Sum() != null )
        {
            params.put( "md5", Hex.encodeHexString( metadata.getMd5Sum() ) );
        }
        params.put( "name", metadata.getName() );
        params.put( "version", metadata.getVersion() );
        return params;
    }


}

