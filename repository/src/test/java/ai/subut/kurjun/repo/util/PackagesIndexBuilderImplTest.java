package ai.subut.kurjun.repo.util;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.ar.DebAr;
import ai.subut.kurjun.ar.DefaultDebAr;
import ai.subut.kurjun.cfparser.DefaultControlFileParser;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.MetadataListing;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.repo.service.PackageFilenameBuilder;
import ai.subut.kurjun.repo.service.PackagesIndexBuilder;


@RunWith( MockitoJUnitRunner.class )
public class PackagesIndexBuilderImplTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger( PackagesIndexBuilderImplTest.class );

    private static Map<String, File> testPackageFiles;
    private static Map<String, SerializableMetadata> metadata;
    private static int filesCount = 2;

    @Mock
    private FileStore fileStore;

    @Mock
    private PackageMetadataStore metadataStore;

    @Mock
    private PackageMetadataStoreFactory metadataStoreFactory;

    @Mock
    private PackageFilenameBuilder filenameBuilder;

    private PackagesIndexBuilderImpl indexBuilder = new PackagesIndexBuilderImpl();
    private PackagesProviderFactory packagesProviderFactory = new PackagesProviderFactory();
    private KurjunContext context = new KurjunContext( "test" );


    @BeforeClass
    public static void setUpClass() throws IOException
    {
        Properties prop = new Properties();
        prop.load( ClassLoader.getSystemResourceAsStream( "test.properties" ) );

        testPackageFiles = new HashMap<>();
        metadata = new HashMap<>();

        for ( int i = 0; i < filesCount; i++ )
        {
            boolean skip = false;
            URL url = new URL( prop.getProperty( "test.pkg.url." + ( i + 1 ) ) );
            File file = new File( prop.getProperty( "test.pkg.file." + ( i + 1 ) ) );

            if ( !file.exists() )
            {
                LOGGER.info( "Test package {} does NOT exist, will download from {}", file, url );
                try ( InputStream is = url.openStream() )
                {
                    Files.copy( is, file.toPath() );
                }
                catch ( IOException ex )
                {
                    LOGGER.error( "Failed to download package from {}", url );
                    skip = true;
                }
            }
            else
            {
                LOGGER.debug( "Test package {} exists, will not download", file );
            }

            if ( !skip )
            {
                PackageMetadata pm = createPackageMetadata( file );
                String md5hex = pm.getMd5Sum();
                metadata.put( md5hex, MetadataUtils.serializablePackageMetadata( pm ) );
                testPackageFiles.put( md5hex, file );
            }
        }
    }


    @Before
    public void setUp() throws IOException
    {
        Assume.assumeFalse( metadata.isEmpty() );
        Assume.assumeFalse( testPackageFiles.isEmpty() );
        Assume.assumeTrue( metadata.size() == testPackageFiles.size() );

        MetadataListing listing = Mockito.mock( MetadataListing.class );
        Mockito.when( listing.getPackageMetadata() ).thenReturn( metadata.values() );
        Mockito.when( listing.isTruncated() ).thenReturn( false );

        Mockito.when( metadataStore.list() ).thenReturn( listing );
        Mockito.when( fileStore.contains( Matchers.any( String.class ) ) ).thenAnswer( new Answer<Boolean>()
        {
            @Override
            public Boolean answer( InvocationOnMock args ) throws Throwable
            {
                return metadata.containsKey( Hex.encodeHexString( ( byte[] ) args.getArguments()[0] ) );
            }
        } );
        Mockito.when( fileStore.get( Matchers.any( String.class ) ) ).thenAnswer( new Answer<InputStream>()
        {
            @Override
            public InputStream answer( InvocationOnMock args ) throws Throwable
            {
                return new FileInputStream(
                        testPackageFiles.get( Hex.encodeHexString( ( byte[] ) args.getArguments()[0] ) ) );
            }
        } );

        indexBuilder.filenameBuilder = filenameBuilder;
        indexBuilder.gson = MetadataUtils.JSON;

        Mockito.when( metadataStoreFactory.create( context ) ).thenReturn( metadataStore );
        packagesProviderFactory.metadataStoreFactory = metadataStoreFactory;
        packagesProviderFactory.gson = MetadataUtils.JSON;
    }


    @After
    public void tearDown()
    {
    }


    @Test
    public void testBuildIndex() throws Exception
    {
        PackagesIndexBuilder.PackagesProvider packs =
                packagesProviderFactory.create( context, "main", Architecture.AMD64 );
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        indexBuilder.buildIndex( packs, os, CompressionType.NONE );

        String s = new String( os.toByteArray() );
        LOGGER.info( "Packages index:\n{}", s );

        // TODO:
        for ( Metadata pm : metadata.values() )
        {
            Assert.assertTrue( s.contains( PackageMetadata.PACKAGE_FIELD + ": " + pm.getName() ) );
            Assert.assertTrue( s.contains( PackageMetadata.VERSION_FIELD + ": " + pm.getVersion() ) );
        }
    }


    private static PackageMetadata createPackageMetadata( File file ) throws IOException
    {
        DefaultControlFileParser cfParser = new DefaultControlFileParser();
        Map<String, Object> map = new HashMap<>();
        map.put( "filename", file.getName() );
        map.put( "md5sum", DigestUtils.md5( new FileInputStream( file ) ) );

        DebAr deb = new DefaultDebAr( file );
        return cfParser.parse( map, deb.getControlFile() );
    }
}

