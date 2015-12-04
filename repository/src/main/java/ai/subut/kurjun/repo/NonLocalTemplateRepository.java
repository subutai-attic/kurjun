package ai.subut.kurjun.repo;


import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.annotation.Nullable;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.NonLocalRepository;
import ai.subut.kurjun.model.security.Identity;
import ai.subut.kurjun.repo.cache.PackageCache;
import ai.subut.kurjun.repo.util.SecureRequestFactory;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.client.ClientException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TODO: Refactor common methods of all non local repos into base one.
 *
 */
public class NonLocalTemplateRepository extends RepositoryBase implements NonLocalRepository
{

    private static final Logger LOGGER = LoggerFactory.getLogger( NonLocalTemplateRepository.class );

    static final String INFO_PATH = "info";
    static final String LIST_PATH = "list";
    static final String GET_PATH = "get";
    static final String TOKEN_PATH = "rest/v1/identity/gettoken";

    @Inject
    private Gson gson;
    private PackageCache cache;

    private final URL url;
    private final Identity identity;

    private boolean useToken = false;


    @Inject
    public NonLocalTemplateRepository( PackageCache cache, @Assisted String url, @Assisted @Nullable Identity identity, @Assisted boolean useToken )
    {
        this.cache = cache;
        this.identity = identity;
        this.useToken = useToken;
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
        throw new UnsupportedOperationException( "Not supported in template repositories." );
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

        Response resp = doGet( webClient );
        if ( resp != null && resp.getStatus() == Response.Status.OK.getStatusCode() )
        {
            if ( resp.getEntity() instanceof InputStream )
            {
                try
                {
                    String json = IOUtils.toString( ( InputStream ) resp.getEntity() );
                    return gson.fromJson( json, DefaultTemplate.class );
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
        WebClient webClient = secreq.makeClient( GET_PATH, makeParamsMap( metadata ) );
        if ( identity != null )
        {
            webClient.header( KurjunConstants.HTTP_HEADER_FINGERPRINT, identity.getKeyFingerprint() );
        }

        Response resp = doGet( webClient );
        if ( resp != null && resp.getStatus() == Response.Status.OK.getStatusCode() )
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
        WebClient webClient = secreq.makeClient( LIST_PATH, makeParamsMap( new DefaultMetadata() ) );
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


    private Response doGet( WebClient webClient )
    {
        try
        {
            return webClient.get();
        }
        catch ( ClientException e )
        {
            LOGGER.warn( "Failed to do GET.", e );
            return null;
        }
    }


    private Map<String, String> makeParamsMap( Metadata metadata )
    {
        Map<String, String> params = MetadataUtils.makeParamsMap( metadata );

        if ( useToken )
        {
            params.put( "sptoken", getRemoteToken() );
        }

        // Set parameter kc=kurjun_client to indicate this request is going from Kurjun
        params.put( "kc", Boolean.TRUE.toString() );

        return params;
    }


    private String getRemoteToken()
    {
        SecureRequestFactory secreq = new SecureRequestFactory( this );
        WebClient webClient = secreq.makeClient( "", TOKEN_PATH,
                ImmutableMap.of( "username", "admin", "password", "secret" ) );

        webClient.accept( MediaType.TEXT_PLAIN );
        Response resp = doGet( webClient );
        if ( resp != null && resp.getStatus() == Response.Status.OK.getStatusCode() )
        {
            return resp.readEntity( String.class );
        }
        else
        {
            throw new RuntimeException( "Unable to get token from the remote server = " + getUrl() + " and path = " + TOKEN_PATH );
        }
    }


    private InputStream checkCache( Metadata metadata )
    {
        if ( metadata.getMd5Sum() != null )
        {
            if ( cache.contains( metadata.getMd5Sum() ) )
            {
                return cache.get( metadata.getMd5Sum() );
            }
        }
        else
        {
            SerializableMetadata m = getPackageInfo( metadata );
            if ( m != null && cache.contains( m.getMd5Sum() ) )
            {
                return cache.get( m.getMd5Sum() );
            }
        }
        return null;
    }


    private byte[] cacheStream( InputStream is )
    {
        Path target = null;
        try
        {
            target = Files.createTempFile( null, null );
            Files.copy( is, target, StandardCopyOption.REPLACE_EXISTING );
            return cache.put( target.toFile() );
        }
        catch ( IOException ex )
        {
            LOGGER.error( "Failed to cache package", ex );
        }
        finally
        {
            if ( target != null )
            {
                target.toFile().delete();
            }
        }
        return null;
    }


    private List<SerializableMetadata> parseItems( String items )
    {
        Type collectionType = new TypeToken<LinkedList<DefaultTemplate>>()
        {
        }.getType();
        return gson.fromJson( items, collectionType );
    }

}
