package ai.subut.kurjun.repo;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.utils.InetUtils;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.annotation.Nullable;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.repo.cache.PackageCache;
import ai.subut.kurjun.repo.util.http.WebClientFactory;


/**
 * Non-local templates repository implementation. <p> TODO: Refactor common methods of all non local repos into base
 * one.
 */
class RemoteTemplateRepository extends RemoteRepositoryBase
{

    private static final Logger LOGGER = LoggerFactory.getLogger( RemoteTemplateRepository.class );

    static final String TEMPLATE_PATH = "template";
    static final String INFO_PATH = "info";
    static final String LIST_PATH = "list";
    static final String GET_PATH = "get";
    static final String MD5_PATH = "md5";


    private WebClientFactory webClientFactory;
    private Gson gson;

    private PackageCache cache;

    private final URL url;
    private final User identity;

    private String token = null;

    private String md5Sum = "";
    private List<SerializableMetadata> remoteIndexChache = new LinkedList<>();


    private static final int CONN_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 1200000;
    private static final int CONN_TIMEOUT_FOR_URL_CHECK = 10000;

    private String context;
    private String search = "all";


    @Inject
    public RemoteTemplateRepository( PackageCache cache, WebClientFactory webClientFactory, Gson gson,
                                     @Assisted( "url" ) String url, @Assisted @Nullable User identity,
                                     @Assisted( "context" ) String kurjunContext,
                                     @Assisted( "token" ) @Nullable String token, @Assisted( "search" ) String search )
    {
        this.search = search;
        this.gson = gson;
        this.webClientFactory = webClientFactory;
        this.cache = cache;
        this.identity = identity;
        this.context = kurjunContext;
        this.token = token;


        try
        {
            this.url = new URL( url );
        }
        catch ( MalformedURLException ex )
        {
            throw new IllegalArgumentException( "Invalid url", ex );
        }
        _initCache();
    }


    private void _initCache()
    {
        this.remoteIndexChache = listPackages();
        this.md5Sum = getMd5();
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
        throw new UnsupportedOperationException( "Not supported in template repositories." );
    }


    @Override
    public SerializableMetadata getPackageInfo( Metadata metadata )
    {
        WebClient webClient =
                webClientFactory.makeSecure( this, TEMPLATE_PATH + "/" + INFO_PATH, makeParamsMap( metadata ) );

        LOGGER.debug( "Trying to get package info from remote Kurjun: {} with Id: {}", url, metadata.getId() );

        if ( identity != null )
        {
            webClient.header( KurjunConstants.HTTP_HEADER_FINGERPRINT, identity.getKeyFingerprint() );
        }

        Response resp = doGet( webClient );

        if ( resp != null && resp.getStatus() == Response.Status.OK.getStatusCode() )
        {
            LOGGER.debug( "Reading remote template info" );

            if ( resp.getEntity() instanceof InputStream )
            {
                try
                {
                    String json = IOUtils.toString( ( InputStream ) resp.getEntity() );

                    return toObject( json );
                }
                catch ( IOException ex )
                {
                    LOGGER.error( "Failed to read response data", ex );
                }
            }
        }

        assert resp != null;

        LOGGER.error( "Did not receive metadata info from remote Kurjun for request: {}, response code: {}",
                metadata.getId(), resp.getStatus() );

        return null;
    }


    @Override
    public InputStream getPackageStream( Metadata metadata, PackageProgressListener progressListener )
    {
        try
        {
            LOGGER.debug( "Trying to get stream from remote Kurjun: {} with Id: {}", url, metadata.getId() );

            InputStream cachedStream = checkCache( metadata );
            if ( cachedStream != null )
            {
                //                BufferedInputStream is = new BufferedInputStream( cachedStream );
                getPackageStream( cachedStream, progressListener );
                return cachedStream;
            }

            URLConnection conn = webClientFactory.openSecureConnection( this, TEMPLATE_PATH + "/" + GET_PATH, makeParamsMap( metadata ) );
            LOGGER.info( "Downloading template file {}", conn.getURL() );
            if ( identity != null )
            {
                conn.setRequestProperty( KurjunConstants.HTTP_HEADER_FINGERPRINT, identity.getKeyFingerprint() );
            }

            ByteArrayOutputStream barrout = getPackageStream( conn, progressListener );
            InputStream inputStream = new ByteArrayInputStream( barrout.toByteArray() );

            String md5Calculated = cacheStream( inputStream );

            // compare the requested and received md5 checksums
            if ( md5Calculated.equals( metadata.getMd5Sum() ) )
            {
                return cache.get( md5Calculated );
            }
            else
            {
                deleteCache( md5Calculated );

                LOGGER.error( "Md5 checksum mismatch after getting the package from remote host. "
                                + "Requested with md5={}, name={}, version={}", metadata.getMd5Sum(), metadata
                                .getName(),
                        metadata.getVersion() );
            }
        }
        catch(IOException e)
        {
            LOGGER.error( "Did not receive metadata info from remote Kurjun for request: " + metadata.getId(), e);
        }
        return null;
    }


    @Override
    public List<SerializableMetadata> listPackages()
    {

        String md5 = getMd5();

        LOGGER.debug( "Trying to get latest md5 sum of remote index. Current md5:{},remote md5:{}", this.md5Sum, md5 );

        if ( this.md5Sum.equalsIgnoreCase( md5 ) )
        {
            LOGGER.debug( "Remote index did not change, returning local index cache" );
            return this.remoteIndexChache;
        }
        Map<String, String> params = makeParamsMap( new DefaultMetadata() );


        //get only public Kurjun local packages
        WebClient webClient = webClientFactory.makeSecure( this, TEMPLATE_PATH + "/" + LIST_PATH, params );
        if ( identity != null )
        {
            webClient.header( KurjunConstants.HTTP_HEADER_FINGERPRINT, identity.getKeyFingerprint() );
        }

        Response resp = doGet( webClient );
        if ( resp != null && resp.getStatus() == Response.Status.OK.getStatusCode() )
        {
            LOGGER.debug( "Reading remote index cache" );
            if ( resp.getEntity() instanceof InputStream )
            {
                try
                {
                    List<String> items = IOUtils.readLines( ( InputStream ) resp.getEntity() );

                    this.remoteIndexChache = toObjectList( items.get( 0 ) );
                    LOGGER.debug( "Remote index contains: {} templates", remoteIndexChache.size() );

                    this.md5Sum = md5;
                    LOGGER.debug( "Updating remote md5 sum to :{}", this.md5Sum );

                    return this.remoteIndexChache;
                }
                catch ( IOException ex )
                {
                    LOGGER.error( "Failed to read packages list", ex );
                }
            }
        }
        assert resp != null;

        LOGGER.error( "Could not obtain remote index, returning empty list for remote Kurjun : {} ", url );

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
        WebClient webClient = webClientFactory.makeSecure( this, TEMPLATE_PATH + "/" + MD5_PATH, null );
        LOGGER.debug( "Getting remote index md5 checksum" );
        Response resp = doGet( webClient );
        if ( resp != null && resp.getStatus() == Response.Status.OK.getStatusCode() )
        {
            LOGGER.debug( "Reading remote index md5 checksum" );
            if ( resp.getEntity() instanceof InputStream )
            {
                try
                {
                    List<String> items = IOUtils.readLines( ( InputStream ) resp.getEntity() );
                    if ( items.size() > 0 )
                    {
                        return items.get( 0 );
                    }
                }
                catch ( IOException ex )
                {
                    LOGGER.error( "Failed to read packages list", ex );
                }
            }
        }
        LOGGER.error( "Failed to fetch remote index md5 checksum" );
        return "";
    }


    @Override
    public List<SerializableMetadata> getCachedData()
    {
        return this.remoteIndexChache;
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
            LOGGER.error( "Failed to do GET.", e );
        }
        return null;
    }


    private Map<String, String> makeParamsMap( Metadata metadata )
    {
        Map<String, String> params = MetadataUtils.makeParamsMap( metadata );
        params.put( "repository", search );
        if ( token != null )
        {
            params.put( "sptoken", token );
        }

        // Set parameter kc=kurjun_client to indicate this request is going from Kurjun
        params.put( "kc", Boolean.TRUE.toString() );

        return params;
    }


    private List<SerializableMetadata> parseItems( String items )
    {
        Type collectionType = new TypeToken<LinkedList<DefaultTemplate>>()
        {
        }.getType();
        return gson.fromJson( items, collectionType );
    }


    private List<SerializableMetadata> toObjectList( String items )
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        try
        {
            return objectMapper.readValue( items, new TypeReference<List<DefaultTemplate>>()
            {
            } );
        }
        catch ( IOException e )
        {
            LOGGER.error( " ***** Failed to convert object", e );
        }
        return null;
    }


    private SerializableMetadata toObject( String items )
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        try
        {
            return objectMapper.readValue( items, DefaultTemplate.class );
        }
        catch ( IOException e )
        {
            LOGGER.error( " ***** Failed to convert object", e );
        }
        return null;
    }


    @Override
    public KurjunContext getContext()
    {
        return null;
    }
}
