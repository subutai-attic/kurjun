package ai.subut.kurjun.repo;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.utils.InetUtils;
import ai.subut.kurjun.index.service.PackagesIndexParser;
import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.index.Checksum;
import ai.subut.kurjun.model.index.ChecksummedResource;
import ai.subut.kurjun.model.index.IndexPackageMetaData;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.RemoteRepository;
import ai.subut.kurjun.repo.cache.PackageCache;
import ai.subut.kurjun.repo.util.PathBuilder;
import ai.subut.kurjun.repo.util.http.WebClientFactory;
import ai.subut.kurjun.riparser.service.ReleaseIndexParser;


/**
 * Nonlocal repository implementation. Remote repositories can be either non-virtual or virtual, this does not matter
 * for {@link RemoteRepository} implementation.
 */
class RemoteAptRepository extends RemoteRepositoryBase
{
    private static final Logger LOGGER = LoggerFactory.getLogger( RemoteAptRepository.class );


    private final URL url;
    private ReleaseIndexParser releaseIndexParser;
    private PackagesIndexParser packagesIndexParser;
    private PackageCache cache;
    @Inject
    Gson gson;

    private WebClientFactory webClientFactory;

    // TODO: Kairat parameterize release path params
    static final String RELEASE_PATH = "/dists/trusty/Release";

    private static final String MD5_PATH = "/md5";
    private static final String DEB_PATH = "deb";

    private static final int CONN_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 1200000;
    private static final int CONN_TIMEOUT_FOR_URL_CHECK = 10000;

    private List<SerializableMetadata> remoteIndexChache = new LinkedList<>();
    private String md5Sum = "";
    private String search = "all";


    /**
     * Constructs nonlocal repository located by the specified URL.
     *
     * @param url URL of the remote repository
     */
    @Inject
    public RemoteAptRepository( @Assisted URL url, WebClientFactory webClientFactory,
                                ReleaseIndexParser releaseIndexParser, PackagesIndexParser packagesIndexParser,
                                PackageCache cache, String search )
    {
        this.releaseIndexParser = releaseIndexParser;
        this.packagesIndexParser = packagesIndexParser;
        this.cache = cache;
        this.url = url;
        this.webClientFactory = webClientFactory;

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

        WebClient webClient = webClientFactory.makeSecure( this, "/" + DEB_PATH + RELEASE_PATH, null );

        Response resp = doGet( webClient );
        if ( resp != null && resp.getStatus() == Response.Status.OK.getStatusCode() )
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
        List<SerializableMetadata> items = listPackages();

        if ( metadata.getMd5Sum() != null )
        {
            return findByMd5( metadata.getMd5Sum(), items );
        }
        else
        {
            return findByName( metadata.getName(), metadata.getVersion(), items );
        }
    }


    @Override
    public InputStream getPackageStream( Metadata metadata, PackageProgressListener progressListener  )
    {
        SerializableMetadata m = getPackageInfo( metadata );
        if ( m == null )
        {
            return null;
        }

        try
        {
            InputStream cachedStream = checkCache( m );
            if ( cachedStream != null )
            {
//                BufferedInputStream is = new BufferedInputStream( cachedStream );
                getPackageStream( cachedStream, progressListener );
                return cachedStream;
            }

            DefaultPackageMetadata pm = gson.fromJson( m.serialize(), DefaultPackageMetadata.class );
            URLConnection conn = webClientFactory.openSecureConnection( this, "/" + DEB_PATH + "/" + pm.getFilename(),
                    null );
            LOGGER.info( "Downloading apt file {}", conn.getURL() );
            ByteArrayOutputStream barrout = getPackageStream( conn, progressListener );
            InputStream inputStream = new ByteArrayInputStream( barrout.toByteArray() );

            String md5Calculated = cacheStream( inputStream );

            // compare the requested and received md5 checksums
            if ( md5Calculated.equals( pm.getMd5Sum() ) )
            {
                return cache.get( md5Calculated );
            }
            else
            {
                deleteCache( md5Calculated );

                LOGGER.error( "Md5 checksum mismatch after getting the package {} from remote host",
                        pm.getFilename() );
            }
        }
        catch ( IOException e )
        {
            LOGGER.error("Error downloading apt file", e);
        }

//        WebClient webClient = webClientFactory.makeSecure( this, "/" + DEB_PATH + "/" + pm.getFilename(), null );
//
//        Response resp = doGet( webClient );
//        if ( resp != null && resp.getStatus() == Response.Status.OK.getStatusCode() )
//        {
//            if ( resp.getEntity() instanceof InputStream )
//            {
//                InputStream inputStream = ( InputStream ) resp.getEntity();
//
//                String md5Calculated = cacheStream( inputStream );
//
//                // compare the requested and received md5 checksums
//                if ( md5Calculated.equals( pm.getMd5Sum() ) )
//                {
//                    return cache.get( md5Calculated );
//                }
//                else
//                {
//                    deleteCache( md5Calculated );
//
//                    LOGGER.error( "Md5 checksum mismatch after getting the package {} from remote host",
//                            pm.getFilename() );
//                }
//            }
//        }
        return null;
    }


    @Override
    public List<SerializableMetadata> listPackages()
    {
        String md5 = getMd5();

        if ( this.md5Sum.equalsIgnoreCase( md5 ) )
        {
            return this.remoteIndexChache;
        }

        List<SerializableMetadata> result = new LinkedList<>();
        Set<ReleaseFile> distributions = getDistributions();

        if ( distributions != null )
        {
            for ( ReleaseFile distr : distributions )
            {
                PathBuilder pb = PathBuilder.instance().setRelease( distr );
                for ( String component : distr.getComponents() )
                {
                    for ( Architecture arch : distr.getArchitectures() )
                    {
                        String path = pb.setResource( makePackagesIndexResource( component, arch ) ).build();
                        List<SerializableMetadata> items = fetchPackagesMetadata( path, component );
                        result.addAll( items );
                    }
                }
            }
            this.md5Sum = md5;
            this.remoteIndexChache = result;
        }
        return result;
    }


    @Override
    protected Logger getLogger()
    {
        return LOGGER;
    }


    @Override
    public String getMd5()
    {

        WebClient webClient = webClientFactory.makeSecure( this, "/" + DEB_PATH + MD5_PATH, null );

        Response resp = doGet( webClient );

        if ( resp != null && resp.getStatus() == Response.Status.OK.getStatusCode() )
        {
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
            LOGGER.warn( "Failed to do GET.", e );
        }
        return null;
    }


    private SerializableMetadata findByMd5( String md5Sum, List<SerializableMetadata> items )
    {
        for ( SerializableMetadata item : items )
        {
            if ( md5Sum.equals( item.getMd5Sum() ) )
            {
                return item;
            }
        }
        return null;
    }


    private SerializableMetadata findByName( String name, String version, List<SerializableMetadata> items )
    {
        Objects.requireNonNull( name, "Package name not specified." );

        if ( version != null )
        {
            for ( SerializableMetadata item : items )
            {
                if ( name.equals( item.getName() ) && version.equals( item.getVersion() ) )
                {
                    return item;
                }
            }
        }
        else
        {
            Comparator<Metadata> cmp = Collections.reverseOrder( MetadataUtils.makeVersionComparator() );
            Object[] arr = items.stream().filter( m -> m.getName().equals( name ) ).sorted( cmp ).toArray();
            if ( arr.length > 0 )
            {
                return ( SerializableMetadata ) arr[0];
            }
        }
        return null;
    }


    private ChecksummedResource makePackagesIndexResource( String component, Architecture architecture )
    {
        return new ChecksummedResource()
        {
            @Override
            public String getRelativePath()
            {
                return String.format( "%s/binary-%s/Packages", component, architecture.toString() );
            }


            @Override
            public long getSize()
            {
                throw new UnsupportedOperationException( "Not to be used." );
            }


            @Override
            public byte[] getChecksum( Checksum type )
            {
                throw new UnsupportedOperationException( "Not to be used." );
            }
        };
    }


    private List<SerializableMetadata> fetchPackagesMetadata( String path, String component )
    {
        WebClient webClient = webClientFactory.makeSecure( this, DEB_PATH + "/" + path, null );

        Response resp = doGet( webClient );
        if ( resp != null && resp.getStatus() == Response.Status.OK.getStatusCode() && resp
                .getEntity() instanceof InputStream )
        {
            try ( InputStream is = ( InputStream ) resp.getEntity() )
            {
                List<IndexPackageMetaData> items = packagesIndexParser.parse( is, CompressionType.NONE, component );

                List<SerializableMetadata> result = new ArrayList<>( items.size() );
                for ( IndexPackageMetaData item : items )
                {
                    result.add( MetadataUtils.serializableIndexPackageMetadata( item ) );
                }
                return result;
            }
            catch ( IOException ex )
            {
                LOGGER.error( "Invalid packages index at {}", path, ex );
            }
        }
        return Collections.emptyList();
    }


    @Override
    public KurjunContext getContext()
    {
        return null;
    }
}

