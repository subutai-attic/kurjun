package ai.subut.kurjun.repo;


import java.io.File;
import java.io.FileInputStream;
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
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.ar.DebAr;
import ai.subut.kurjun.ar.DefaultDebAr;
import ai.subut.kurjun.ar.SubutaiDebAr;
import ai.subut.kurjun.cfparser.service.ControlFileParser;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.utils.InetUtils;
import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.model.index.IndexPackageMetaData;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.riparser.DefaultRelease;
import ai.subut.kurjun.storage.factory.FileStoreFactory;
import ai.subut.kurjun.subutai.service.SubutaiTemplateParser;


/**
 * Local virtual apt repository implementation.
 *
 */
class LocalAptRepository extends LocalRepositoryBase
{

    private static final Logger LOGGER = LoggerFactory.getLogger( LocalAptRepository.class );

    private ControlFileParser controlFileParser;
    private SubutaiTemplateParser templateParser;
    private FileStoreFactory fileStoreFactory;
    private PackageMetadataStoreFactory metadataStoreFactory;

    private final KurjunContext context;

    private final URL url;
    private Set<ReleaseFile> releases = new HashSet<>();


    @Inject
    public LocalAptRepository(
            ControlFileParser controlFileParser,
            SubutaiTemplateParser templateParser,
            FileStoreFactory fileStoreFactory,
            PackageMetadataStoreFactory metadataStoreFactory,
            @Assisted KurjunContext kurjunContext )
    {
        // TODO: setup mechanism for repos
        DefaultRelease r = new DefaultRelease();
        r.setCodename( "trusty" );
        r.setArchitectures( Arrays.asList( Architecture.ALL, Architecture.AMD64, Architecture.i386 ) );
        r.setComponents( Arrays.asList( "main", "contrib", "non-free" ) );
        r.setDescription( "Kurjun virtual apt repository" );
        r.setVersion( "12.04" );
        releases.add( r );

        this.controlFileParser = controlFileParser;
        this.templateParser = templateParser;
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
    public PackageMetadata put( InputStream is, CompressionType compressionType ) throws IOException
    {
        PackageMetadataStore metadataStore = metadataStoreFactory.create( context );
        FileStore fileStore = fileStoreFactory.create( context );

        String ext = CompressionType.makeFileExtenstion( compressionType );
        Path target = Files.createTempFile( null, ext );
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

            DefaultPackageMetadata metadata = MetadataUtils.serializablePackageMetadata( meta );
            addExtraData( metadata, target );
            addSubutaiData( metadata, target );

            metadataStore.put( metadata );
            fileStore.put( target.toFile() );
            return meta;
        }
        catch ( Exception ex )
        {
            throw new IOException( ex.getMessage(), ex );
        }
        finally
        {
            Files.delete( target );
            FileUtils.deleteDirectory( tempDir.toFile() );
        }
    }


    @Override
    public Metadata put( final InputStream is, final CompressionType compressionType, final String owner )
            throws IOException
    {
        return null;
    }


    @Override
    public Metadata put( final File file, final CompressionType compressionType, final String owner ) throws IOException
    {
        return null;
    }


    @Override
    protected Logger getLogger()
    {
        return LOGGER;
    }


    @Override
    protected PackageMetadataStore getMetadataStore()
    {
        return metadataStoreFactory.create( context );
    }


    @Override
    protected FileStore getFileStore()
    {
        return fileStoreFactory.create( context );
    }


    private void addExtraData( DefaultPackageMetadata metadata, Path packageFile ) throws IOException
    {
        long totalBytes = 0;
        MessageDigest sha1 = DigestUtils.getSha1Digest();
        MessageDigest sha2 = DigestUtils.getSha256Digest();
        try ( InputStream is = new FileInputStream( packageFile.toFile() ) )
        {
            int n;
            byte[] buf = new byte[1024 * 8];
            while ( ( n = is.read( buf ) ) > 0 )
            {
                totalBytes += n;
                sha1.update( buf, 0, n );
                sha2.update( buf, 0, n );
            }
        }

        metadata.getExtra().put( IndexPackageMetaData.SIZE_FIELD, Long.toString( totalBytes ) );
        metadata.getExtra().put( IndexPackageMetaData.SHA1_FIELD, Hex.encodeHexString( sha1.digest() ) );
        metadata.getExtra().put( IndexPackageMetaData.SHA256_FIELD, Hex.encodeHexString( sha2.digest() ) );
    }


    /**
     * This method searches for Subutai configuration file in Debian package located at supplied path. If Debian package
     * contains such a file, a file named "config", it is parsed and extracted data is stored as extra meta data for the
     * package.
     *
     * @param metadata meta data instance to add data to
     * @param packageFile Debian package file to check
     * @throws IOException
     */
    private void addSubutaiData( DefaultPackageMetadata metadata, Path packageFile ) throws IOException
    {
        File tempDir = Files.createTempDirectory( null ).toFile();
        InputStream is = null;
        try
        {
            SubutaiDebAr deb = new SubutaiDebAr( packageFile.toFile(), tempDir );
            File configFile = deb.getConfigFile();
            if ( configFile != null )
            {
                is = new FileInputStream( configFile );
                SubutaiTemplateMetadata m = templateParser.parseTemplateConfigFile( is );
                metadata.getExtra().putAll( m.getExtra() );
            }
        }
        finally
        {
            IOUtils.closeQuietly( is );
            FileUtils.deleteQuietly( tempDir );
        }
    }


    @Override
    public KurjunContext getContext()
    {
        return context;
    }
}

