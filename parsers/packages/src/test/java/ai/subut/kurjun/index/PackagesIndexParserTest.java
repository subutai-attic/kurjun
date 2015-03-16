package ai.subut.kurjun.index;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.subut.kurjun.index.impl.PackagesIndexParserImpl;
import ai.subut.kurjun.model.index.IndexPackageMetaData;


public class PackagesIndexParserTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger( PackagesIndexParserTest.class );
    private static File gzIndexFile;
    private static File bz2IndexFile;

    private PackagesIndexParser parser = new PackagesIndexParserImpl();


    @BeforeClass
    public static void setUpClass() throws IOException
    {
        Properties prop = new Properties();
        prop.load( ClassLoader.getSystemResourceAsStream( "test.properties" ) );

        String buildDir = prop.getProperty( "project.build.directory", "target" );

        gzIndexFile = Paths.get( buildDir, "Packages.gz" ).toFile();
        URL gzUrl = new URL( prop.getProperty( "test.pkg.index.url.gz" ) );
        if ( !gzIndexFile.exists() )
        {
            LOGGER.info( "Packages index {} does NOT exist, will download from {}", gzIndexFile, gzUrl );
            try ( InputStream is = gzUrl.openStream() )
            {
                Files.copy( is, gzIndexFile.toPath() );
            }
            catch ( IOException ex )
            {
                LOGGER.error( "Failed to download package index from ", gzUrl );
            }
        }
        else
        {
            LOGGER.debug( "Packages index {} exists, will not download", gzIndexFile );
        }

        bz2IndexFile = Paths.get( buildDir, "Packages.bz2" ).toFile();
        URL bz2url = new URL( prop.getProperty( "test.pkg.index.url.bz2" ) );
        if ( !bz2IndexFile.exists() )
        {
            LOGGER.info( "Packages index {} does NOT exist, will download from {}", bz2IndexFile, bz2url );
            try ( InputStream is = bz2url.openStream() )
            {
                Files.copy( is, bz2IndexFile.toPath() );
            }
            catch ( IOException ex )
            {
                LOGGER.error( "Failed to download package index from {}", bz2url );
            }
        }
        else
        {
            LOGGER.debug( "Packages index {} exists, will not download", bz2IndexFile );
        }
    }


    @Before
    public void setUp() throws IOException
    {
    }


    @After
    public void tearDown()
    {
    }


    @Test
    public void testParseGz() throws Exception
    {
        Assume.assumeTrue( gzIndexFile.exists() );
        List<IndexPackageMetaData> items = parser.parse( gzIndexFile );

        // TODO: do real assertions
        Assert.assertFalse( items.isEmpty() );
    }


    @Test
    public void testParseBz2() throws Exception
    {
        Assume.assumeTrue( bz2IndexFile.exists() );
        List<IndexPackageMetaData> items = parser.parse( bz2IndexFile );

        // TODO: do real assertions
        Assert.assertFalse( items.isEmpty() );
    }
}

