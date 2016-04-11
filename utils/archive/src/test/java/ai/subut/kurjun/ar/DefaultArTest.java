package ai.subut.kurjun.ar;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipUtils;
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.io.FileUtils;


/**
 * DefaultAr Tester.
 */
public class DefaultArTest {
    private static final Logger LOG = LoggerFactory.getLogger( DefaultArTest.class );

    private static String BUILD_DIRECTORY;
    private static File TEST_PKG_FILE;


    /**
     * Prepares the test package[s] for use by the tests.
     */
    @BeforeClass
    public static void getTestPackages() throws Exception {
        // first let's load the resource properties file for tests
        Properties props = new Properties();
        props.load( ClassLoader.getSystemResourceAsStream("test.properties") );

        // set the needed properties from it
        TEST_PKG_FILE = new File( props.getProperty( "test.pkg.file", "UNKNOWN" ) );
        String TEST_PKG_URL = props.getProperty( "test.pkg.url", "UNKNOWN" );
        BUILD_DIRECTORY = props.getProperty( "project.build.directory", "target" );

        // check if the test package file is present, if not download
        if ( ! TEST_PKG_FILE.exists() ) {
            LOG.info( "Test package {} does NOT exist, will download from:\n{}", TEST_PKG_FILE, TEST_PKG_URL );
            FileUtils.copyURLToFile( new URL( TEST_PKG_URL ), TEST_PKG_FILE );
        }
        else
        {
            LOG.debug( "Test package {} exists, will not download.", TEST_PKG_FILE );
        }

    }


    /**
     * Method: list(File ar)
     */
    @Test
    public void testList() throws Exception {
        DefaultAr ar = new DefaultAr( TEST_PKG_FILE );
        for ( ArArchiveEntry entry : ar.list() ) {
            LOG.debug( "entry = {}", entry.getName() );

            if ( entry.getName().startsWith( "control.tar" ) )
            {
                File compressedAr = new File( BUILD_DIRECTORY, entry.getName() );
                File uncompressedAr = new File( BUILD_DIRECTORY,
                        GzipUtils.getUncompressedFilename( entry.getName() ) );

                ar.extract( compressedAr, entry );

                CompressionType compressionType = CompressionType.getCompressionType( entry.getName() );
                InputStream in;

                switch ( compressionType )
                {
                    case XZ:
                        in = new XZCompressorInputStream( new FileInputStream( compressedAr ) );
                        ar.extractFromStream( uncompressedAr, in, -1 );
                        in.close();
                        break;
                    case GZIP:
                        in = new GZIPInputStream( new FileInputStream( compressedAr ) );
                        ar.extractFromStream( uncompressedAr, in, -1 );
                        in.close();
                        break;
                    case BZIP2:
                        in = new BZip2CompressorInputStream( new FileInputStream( compressedAr ) );
                        ar.extractFromStream( uncompressedAr, in, -1 );
                        in.close();
                    case LZMA:
                        in = new LZMACompressorInputStream( new FileInputStream( compressedAr ) );
                        ar.extractFromStream( uncompressedAr, in, -1 );
                        in.close();
                    case NONE:
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
