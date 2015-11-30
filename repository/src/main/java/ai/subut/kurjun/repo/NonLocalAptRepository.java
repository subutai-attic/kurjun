package ai.subut.kurjun.repo;


import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.NonLocalRepository;
import ai.subut.kurjun.model.security.Identity;
import ai.subut.kurjun.repo.cache.PackageCache;
import ai.subut.kurjun.repo.http.HttpHandler;
import ai.subut.kurjun.repo.util.SecureRequestFactory;
import ai.subut.kurjun.riparser.service.ReleaseIndexParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;


/**
 * Nonlocal repository implementation. Remote repositories can be either
 * non-virtual or virtual, this does not matter for {@link NonLocalRepository}
 * implementation.
 *
 */
class NonLocalAptRepository extends RepositoryBase implements NonLocalRepository
{
    private static final Logger LOGGER = LoggerFactory.getLogger( NonLocalAptRepository.class );

    private final HttpHandler httpHandler = new HttpHandler( this );
    private final URL url;
    private final ReleaseIndexParser releaseIndexParser;
    private final PackageCache cache;

    static final String INFO_PATH = "info";
    static final String GET_PATH = "get";
    static final String LIST_PATH = "list";

    // TODO: Kairat parameterize release path params
    static final String RELEASE_PATH = "dists/trusty/Release";


    /**
     * Constructs nonlocal repository located by the specified URL.
     *
     * @param releaseIndexParser
     * @param url URL of the remote repository
     */
    @Inject
    public NonLocalAptRepository( PackageCache cache, ReleaseIndexParser releaseIndexParser, @Assisted URL url )
    {
        this.releaseIndexParser = releaseIndexParser;
        this.url = url;
        this.cache = cache;
    }


    @Override
    public Identity getIdentity()
    {
        return null;
    }


    @Override
    public URL getUrl()
    {
        return url;
    }


    @Override
    public boolean isKurjun()
    {
        // TODO: how to define if remote repo is Kurjun or not
        return false;
    }


    @Override
    public Set<ReleaseFile> getDistributions()
    {
        
        SecureRequestFactory secreq = new SecureRequestFactory( this );
        WebClient webClient = secreq.makeClient( RELEASE_PATH, null );

        Response resp = webClient.get();
        if ( resp.getStatus() == Response.Status.OK.getStatusCode() )
        {
            if ( resp.getEntity() instanceof InputStream )
            {
                try
                {
                    ReleaseFile releaseFile = releaseIndexParser.parse( ( InputStream ) resp.getEntity() );
                    Set<ReleaseFile> rs = new HashSet<>();
                    rs.add( releaseFile );
                    return rs;
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
    public SerializableMetadata getPackageInfo( Metadata metadata )
    {
        SecureRequestFactory secreq = new SecureRequestFactory( this );
        WebClient webClient = secreq.makeClient( INFO_PATH, MetadataUtils.makeParamsMap( metadata ) );

        Response resp = webClient.get();
        if ( resp.getStatus() == Response.Status.OK.getStatusCode() )
        {
            if ( resp.getEntity() instanceof InputStream )
            {
                try
                {
                    String json = IOUtils.toString( ( InputStream ) resp.getEntity() );
                    return MetadataUtils.JSON.fromJson( json, DefaultPackageMetadata.class );
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


    @Override
    public List<SerializableMetadata> listPackages()
    {
        throw new UnsupportedOperationException( "TODO: retrieve packages index and parse." );
    }


    protected InputStream openReleaseIndexFileStream( String release ) throws IOException
    {
        return httpHandler.streamReleaseIndexFile( release, false );
    }

}
