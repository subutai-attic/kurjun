package ai.subut.kurjun.index;


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
import org.mockito.InjectMocks;
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

import ai.subut.kurjun.ar.DebAr;
import ai.subut.kurjun.ar.DefaultDebAr;
import ai.subut.kurjun.cfparser.impl.DefaultControlFileParser;
import ai.subut.kurjun.index.impl.PackagesIndexBuilderImpl;
import ai.subut.kurjun.model.metadata.PackageMetadata;
import ai.subut.kurjun.model.metadata.PackageMetadataListing;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.storage.FileStore;


@RunWith( MockitoJUnitRunner.class )
public class PackagesIndexBuilderTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger( PackagesIndexBuilderTest.class );

    private static Map<String, File> testPackageFiles;
    private static Map<String, PackageMetadata> metadata;
    private static int filesCount = 2;

    @Mock
    private FileStore fileStore;

    @Mock
    private PackageMetadataStore metadataStore;

    @InjectMocks
    private PackagesIndexBuilderImpl indexBuilder;


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
                String md5hex = Hex.encodeHexString( pm.getMd5Sum() );
                metadata.put( md5hex, pm );
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

        PackageMetadataListing listing = Mockito.mock( PackageMetadataListing.class );
        Mockito.when( listing.getPackageMetadata() ).thenReturn( metadata.values() );
        Mockito.when( listing.isTruncated() ).thenReturn( false );

        Mockito.when( metadataStore.list() ).thenReturn( listing );
        Mockito.when( fileStore.contains( Matchers.any( byte[].class ) ) ).thenAnswer( new Answer<Boolean>()
        {
            @Override
            public Boolean answer( InvocationOnMock args ) throws Throwable
            {
                return metadata.containsKey( Hex.encodeHexString( ( byte[] ) args.getArguments()[0] ) );
            }
        } );
        Mockito.when( fileStore.get( Matchers.any( byte[].class ) ) ).thenAnswer( new Answer<InputStream>()
        {
            @Override
            public InputStream answer( InvocationOnMock args ) throws Throwable
            {
                return new FileInputStream( testPackageFiles.get(
                        Hex.encodeHexString( ( byte[] ) args.getArguments()[0] ) ) );
            }
        } );
    }


    @After
    public void tearDown()
    {
    }


    @Test
    public void testBuildIndex() throws Exception
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        indexBuilder.buildIndex( os );

        String s = new String( os.toByteArray() );
        LOGGER.info( "Packages index:\n{}", s );

        // TODO:
        for ( PackageMetadata pm : metadata.values() )
        {
            Assert.assertTrue( s.contains( PackageMetadata.PACKAGE_FIELD + ": " + pm.getPackage() ) );
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

