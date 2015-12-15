package ai.subut.kurjun.repo;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
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

import org.apache.cxf.jaxrs.client.WebClient;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.index.service.PackagesIndexParser;
import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.index.Checksum;
import ai.subut.kurjun.model.index.ChecksummedResource;
import ai.subut.kurjun.model.index.IndexPackageMetaData;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.NonLocalRepository;
import ai.subut.kurjun.model.security.Identity;
import ai.subut.kurjun.repo.cache.PackageCache;
import ai.subut.kurjun.repo.http.PathBuilder;
import ai.subut.kurjun.repo.util.SecureRequestFactory;
import ai.subut.kurjun.riparser.service.ReleaseIndexParser;


/**
 * Nonlocal repository implementation. Remote repositories can be either non-virtual or virtual, this does not matter
 * for {@link NonLocalRepository} implementation.
 *
 */
class NonLocalAptRepository extends RepositoryBase implements NonLocalRepository
{
    private static final Logger LOGGER = LoggerFactory.getLogger( NonLocalAptRepository.class );

    private final URL url;

    @Inject
    ReleaseIndexParser releaseIndexParser;

    @Inject
    PackagesIndexParser packagesIndexParser;

    @Inject
    Gson gson;

    @Inject
    PackageCache cache;

    // TODO: Kairat parameterize release path params
    static final String RELEASE_PATH = "dists/trusty/Release";


    /**
     * Constructs nonlocal repository located by the specified URL.
     *
     * @param url URL of the remote repository
     */
    @Inject
    public NonLocalAptRepository( @Assisted URL url )
    {
        this.url = url;
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
    public InputStream getPackageStream( Metadata metadata )
    {
        SerializableMetadata m = getPackageInfo( metadata );
        if ( m == null )
        {
            return null;
        }

        InputStream cachedStream = checkCache( m );
        if ( cachedStream != null )
        {
            return cachedStream;
        }

        DefaultPackageMetadata pm = gson.fromJson( m.serialize(), DefaultPackageMetadata.class );

        SecureRequestFactory secreq = new SecureRequestFactory( this );
        WebClient webClient = secreq.makeClient( pm.getFilename(), null );

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
        List<SerializableMetadata> result = new LinkedList<>();
        Set<ReleaseFile> distributions = getDistributions();

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
        return result;
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


    private SerializableMetadata findByMd5( byte[] md5Sum, List<SerializableMetadata> items )
    {
        for ( SerializableMetadata item : items )
        {
            if ( Arrays.equals( item.getMd5Sum(), md5Sum ) )
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
        SecureRequestFactory secreq = new SecureRequestFactory( this );
        WebClient webClient = secreq.makeClient( path, null );

        Response resp = webClient.get();
        if ( resp.getStatus() == Response.Status.OK.getStatusCode() && resp.getEntity() instanceof InputStream )
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

}

