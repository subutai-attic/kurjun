package ai.subut.kurjun.repo;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.snap.DefaultSnapMetadata;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.annotation.Nullable;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.security.Identity;
import ai.subut.kurjun.repo.cache.PackageCache;
import ai.subut.kurjun.repo.util.SecureRequestFactory;


/**
 * Non-local snap repository implementation.
 *
 */
class NonLocalSnapRepository extends NonLocalRepositoryBase
{

    private static final Logger LOGGER = LoggerFactory.getLogger( NonLocalSnapRepository.class );

    static final String INFO_PATH = "info";
    static final String GET_PATH = "get";
    static final String LIST_PATH = "list";

    @Inject
    private Gson gson;
    private PackageCache cache;

    private final URL url;
    private final Identity identity;


    @Inject
    public NonLocalSnapRepository( PackageCache cache, @Assisted String url, @Assisted @Nullable Identity identity )
    {
        this.cache = cache;
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
        WebClient webClient = secreq.makeClient( INFO_PATH, MetadataUtils.makeParamsMap( metadata ) );
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
        InputStream cachedStream = checkCache( metadata );
        if ( cachedStream != null )
        {
            return cachedStream;
        }

        SecureRequestFactory secreq = new SecureRequestFactory( this );
        WebClient webClient = secreq.makeClient( GET_PATH, MetadataUtils.makeParamsMap( metadata ) );
        if ( identity != null )
        {
            webClient.header( KurjunConstants.HTTP_HEADER_FINGERPRINT, identity.getKeyFingerprint() );
        }

        Response resp = webClient.get();
        if ( resp.getStatus() == Response.Status.OK.getStatusCode() )
        {
            if ( resp.getEntity() instanceof InputStream )
            {
                byte[] md5 = cacheStream( ( InputStream ) resp.getEntity() );
                return cache.get( md5 );
            }
        }
        return null;
    }


    @Override
    public List<SerializableMetadata> listPackages()
    {
        SecureRequestFactory secreq = new SecureRequestFactory( this );
        WebClient webClient = secreq.makeClient( INFO_PATH, null );
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
                    List<String> items = IOUtils.readLines( ( InputStream ) resp.getEntity() );
                    return parseItems( items );
                }
                catch ( IOException ex )
                {
                    LOGGER.error( "Failed to read packages list", ex );
                }
            }
        }
        return Collections.emptyList();
    }


    @Override
    protected Logger getLogger()
    {
        return LOGGER;
    }


    private List<SerializableMetadata> parseItems( List<String> items )
    {
        List<SerializableMetadata> ls = new LinkedList<>();
        for ( String item : items )
        {
            DefaultMetadata m = gson.fromJson( item, DefaultMetadata.class );
            ls.add( m );
        }
        return ls;
    }

}

