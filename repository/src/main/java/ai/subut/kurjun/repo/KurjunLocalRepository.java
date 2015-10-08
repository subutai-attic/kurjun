package ai.subut.kurjun.repo;


import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.ar.DebAr;
import ai.subut.kurjun.ar.DefaultDebAr;
import ai.subut.kurjun.cfparser.service.ControlFileParser;
import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.common.utils.InetUtils;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.riparser.DefaultRelease;
import ai.subut.kurjun.storage.factory.FileStoreFactory;


/**
 * Virtual apt repository implementation.
 *
 */
class KurjunLocalRepository extends RepositoryBase implements LocalRepository
{

    private ControlFileParser controlFileParser;
    private FileStoreFactory fileStoreFactory;
    private PackageMetadataStoreFactory metadataStoreFactory;

    private final KurjunContext context;

    private final URL url;
    private Set<ReleaseFile> releases = new HashSet<>();


    @Inject
    public KurjunLocalRepository(
            ControlFileParser controlFileParser,
            FileStoreFactory fileStoreFactory,
            PackageMetadataStoreFactory metadataStoreFactory,
            @Assisted KurjunContext kurjunContext )
    {
        // TODO: setup mechanism for repos
        DefaultRelease r = new DefaultRelease();
        r.setCodename( "trusty" );
        r.setArchitectures( Arrays.asList( Architecture.AMD64, Architecture.i386 ) );
        r.setComponents( Arrays.asList( "main" ) );
        r.setDescription( "Short description of the repo" );
        r.setVersion( "12.04" );
        releases.add( r );

        this.controlFileParser = controlFileParser;
        this.fileStoreFactory = fileStoreFactory;
        this.metadataStoreFactory = metadataStoreFactory;
        this.context = kurjunContext;

        try
        {
            List<InetAddress> ips = InetUtils.getLocalIPAddresses();
            this.url = new URL( "http", ips.get( 0 ).getHostAddress(), "" );
        }
        catch ( SocketException | MalformedURLException | IndexOutOfBoundsException ex )
        {
            throw new IllegalStateException( ex );
        }
    }


    @Override
    public Path getBaseDirectory()
    {
        throw new UnsupportedOperationException( "Local virtual reposiitory does not have base directory." );
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
        return releases;
    }


    @Override
    public PackageMetadata put( InputStream is ) throws IOException
    {
        PackageMetadataStore metadataStore = metadataStoreFactory.create( context );
        FileStore fileStore = fileStoreFactory.create( context );

        Path target = Files.createTempFile( null, null );
        Path tempDir = Files.createTempDirectory( null );

        try ( DigestInputStream wrapped = new DigestInputStream( is, DigestUtils.getMd5Digest() ) )
        {
            Files.copy( wrapped, target, StandardCopyOption.REPLACE_EXISTING );
            byte[] md5 = wrapped.getMessageDigest().digest();

            Map<String, Object> params = new HashMap<>();
            params.put( "md5sum", md5 );
            params.put( "filename", "" );

            DebAr deb = new DefaultDebAr( target.toFile(), tempDir.toFile() );
            PackageMetadata meta = controlFileParser.parse( params, deb.getControlFile() );

            // TODO: we need release and component supplied here!!!

            metadataStore.put( MetadataUtils.serializablePackageMetadata( meta ) );
            fileStore.put( target.toFile() );
            return meta;
        }
        catch ( ParseException ex )
        {
            throw new IOException( "Failed to parse control file", ex );
        }
        finally
        {
            Files.delete( target );
            FileUtils.deleteDirectory( tempDir.toFile() );
        }
    }

}

