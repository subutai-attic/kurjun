package ai.subut.kurjun.repo;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
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
import org.apache.cxf.transport.http.HTTPConduit;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.utils.InetUtils;
import ai.subut.kurjun.metadata.common.raw.RawMetadata;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.annotation.Nullable;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.security.Identity;
import ai.subut.kurjun.repo.cache.PackageCache;
import ai.subut.kurjun.repo.util.http.WebClientFactory;


public class RemoteRawRepository extends RemoteRepositoryBase
{

    private static final Logger LOGGER = LoggerFactory.getLogger( RemoteRawRepository.class );

    static final String INFO_PATH = "info";
    static final String GET_PATH = "get";
    static final String LIST_PATH = "list";
    private final String MD5_PATH = "md5";

    @Inject
    private WebClientFactory webClientFactory;

    @Inject
    private Gson gson;
    private PackageCache cache;

    private final URL url;
    private final Identity identity;

    private static final int CONN_TIMEOUT = 3000;
    private static final int READ_TIMEOUT = 3000;
    private static final int CONN_TIMEOUT_FOR_URL_CHECK = 200;


    @Inject
    public RemoteRawRepository( PackageCache cache, @Assisted( "url" ) String url,
                                @Assisted @Nullable Identity identity )
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
    public SerializableMetadata getPackageInfo( Metadata metadata )
    {
        WebClient webClient = webClientFactory.make( this, INFO_PATH, MetadataUtils.makeParamsMap( metadata ) );
        if ( identity != null )
        {
            webClient.header( KurjunConstants.HTTP_HEADER_FINGERPRINT, identity.getKeyFingerprint() );
        }

        Response resp = doGet( webClient );
        if ( resp != null && resp.getStatus() == Response.Status.OK.getStatusCode() )
        {
            if ( resp.getEntity() instanceof InputStream )
            {
                try
                {
                    String json = IOUtils.toString( ( InputStream ) resp.getEntity() );
                    return gson.fromJson( json, RawMetadata.class );
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

        WebClient webClient = webClientFactory.make( this, GET_PATH, MetadataUtils.makeParamsMap( metadata ) );
        if ( identity != null )
        {
            webClient.header( KurjunConstants.HTTP_HEADER_FINGERPRINT, identity.getKeyFingerprint() );
        }

        Response resp = doGet( webClient );
        if ( resp != null && resp.getStatus() == Response.Status.OK.getStatusCode() )
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
                                    + "Requested with md5={}, name={}",
                            Hex.toHexString( metadata.getMd5Sum() ), metadata.getName() );
                }
            }
        }
        return null;
    }


    @Override
    public List<SerializableMetadata> listPackages()
    {
        WebClient webClient = webClientFactory.make( this, LIST_PATH, null );
        if ( identity != null )
        {
            webClient.header( KurjunConstants.HTTP_HEADER_FINGERPRINT, identity.getKeyFingerprint() );
        }

        Response resp = doGet( webClient );
        if ( resp != null && resp.getStatus() == Response.Status.OK.getStatusCode() )
        {
            if ( resp.getEntity() instanceof InputStream )
            {
                try
                {
                    List<String> items = IOUtils.readLines( ( InputStream ) resp.getEntity() );
                    return parseItems( items.get( 0 ) );
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
        WebClient webClient = webClientFactory.make( this, MD5_PATH, null );

        Response resp = doGet( webClient );

        if ( resp != null && resp.getStatus() == Response.Status.OK.getStatusCode() )
        {
            String md5 = resp.getEntity().toString();
            if ( md5 != null )
            {
                return md5;
            }
        }
        return "";
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
        throw new UnsupportedOperationException( "Not supported in raw repositories." );
    }


    private List<SerializableMetadata> parseItems( String items )
    {
        Type collectionType = new TypeToken<LinkedList<RawMetadata>>()
        {
        }.getType();
        return gson.fromJson( items, collectionType );
    }


    private Response doGet( WebClient webClient )
    {
        try
        {
            URI remote = webClient.getCurrentURI();

            if ( InetUtils.isHostReachable( remote.getHost(), remote.getPort(), CONN_TIMEOUT_FOR_URL_CHECK ) )
            {
                HTTPConduit httpConduit = ( HTTPConduit ) WebClient.getConfig( webClient ).getConduit();
                httpConduit.getClient().setConnectionTimeout( CONN_TIMEOUT );
                httpConduit.getClient().setReceiveTimeout( READ_TIMEOUT );
                return webClient.get();
            }
            else
            {
                LOGGER.warn( "Remote host is not reachable {}:{}", remote.getHost(), remote.getPort() );
            }
        }
        catch ( Exception e )
        {
            LOGGER.warn( "Failed to do GET.", e );
        }
        return null;
    }


    @Override
    public KurjunContext getContext()
    {
        return null;
    }
}
