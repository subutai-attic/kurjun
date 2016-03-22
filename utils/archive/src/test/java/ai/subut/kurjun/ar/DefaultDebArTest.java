package ai.subut.kurjun.ar;


import java.io.File;
import java.net.URL;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.FileUtils;


/**
 * Tests the default DebAr implementation.
 */
public class DefaultDebArTest
{
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


    @Test
    public void testDebAr() throws Exception
    {
        DefaultDebAr ar = new DefaultDebAr( TEST_PKG_FILE );
    }
}
