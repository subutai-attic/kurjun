package ai.subut.kurjun.repo;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.snap.DefaultSnapMetadata;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.annotation.Nullable;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.repo.cache.PackageCache;
import ai.subut.kurjun.repo.util.http.WebClientFactory;


/**
 * Non-local snap repository implementation.
 *
 */
class RemoteSnapRepository extends RemoteRepositoryBase
{

    private static final Logger LOGGER = LoggerFactory.getLogger( RemoteSnapRepository.class );

    static final String INFO_PATH = "info";
    static final String GET_PATH = "get";
    static final String LIST_PATH = "list";

    @Inject
    private WebClientFactory webClientFactory;

    @Inject
    private Gson gson;
    private PackageCache cache;

    private final URL url;
    private final User identity;


    @Inject
    public RemoteSnapRepository( PackageCache cache, @Assisted String url, @Assisted @Nullable User identity )
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
    public User getIdentity()
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
        WebClient webClient = webClientFactory.makeSecure( this, INFO_PATH, MetadataUtils.makeParamsMap( metadata ) );
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

        WebClient webClient = webClientFactory.makeSecure( this, GET_PATH, MetadataUtils.makeParamsMap( metadata ) );
        if ( identity != null )
        {
            webClient.header( KurjunConstants.HTTP_HEADER_FINGERPRINT, identity.getKeyFingerprint() );
        }

        Response resp = webClient.get();
        if ( resp.getStatus() == Response.Status.OK.getStatusCode() )
        {
            if ( resp.getEntity() instanceof InputStream )
            {
                InputStream inputStream = ( InputStream ) resp.getEntity();

                byte[] md5Calculated = cacheStream( inputStream );

                // compare the requested and received md5 checksums
                if ( Arrays.equals( metadata.getMd5Sum(), md5Calculated ) )
                {
                    return cache.get( md5Calculated );
                }
                else
                {
                    deleteCache( md5Calculated );

                    LOGGER.error(
                            "Md5 checksum mismatch after getting the package from remote host. "
                            + "Requested with md5={}, name={}, version={}",
                            Hex.toHexString( metadata.getMd5Sum() ), metadata.getName(), metadata.getVersion() );
                }
            }
        }
        return null;
    }


    @Override
    public List<SerializableMetadata> listPackages()
    {
        WebClient webClient = webClientFactory.makeSecure( this, INFO_PATH, null );
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


    @Override
    public String getMd5()
    {
        return null;
    }


    @Override
    public List<SerializableMetadata> getCachedData()
    {
        return null;
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


    @Override
    public KurjunContext getContext()
    {
        return null;
    }
}

