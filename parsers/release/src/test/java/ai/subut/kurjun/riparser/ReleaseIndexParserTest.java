package ai.subut.kurjun.riparser;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.riparser.service.ReleaseIndexParser;


public class ReleaseIndexParserTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ReleaseIndexParserTest.class );

    private static String codename;
    private static Path releaseFilePath;
    private static Path signaturePath;
    private static Path keyringPath;

    private ReleaseIndexParser parser;


    @BeforeClass
    public static void setUpClass() throws IOException
    {

        Properties properties = new Properties();
        properties.load( ClassLoader.getSystemResourceAsStream( "test.properties" ) );

        String buildDir = properties.getProperty( "project.build.directory", "target" );
        codename = properties.getProperty( "test.release.codename" );

        // download release file
        releaseFilePath = Paths.get( buildDir, "Release" );
        if ( !Files.exists( releaseFilePath ) )
        {
            URL url = new URL( properties.getProperty( "test.release.url" ) );
            LOGGER.info( "Release file does not exist, will download from {}", url );
            try ( InputStream is = url.openStream() )
            {
                Files.copy( is, releaseFilePath );
            }
        }
        else
        {
            LOGGER.debug( "Release file already exists, will not download" );
        }

        // download release file signature
        signaturePath = Paths.get( buildDir, "Release.gpg" );
        if ( !Files.exists( signaturePath ) )
        {
            URL url = new URL( properties.getProperty( "test.release.sign.url" ) );
            LOGGER.info( "Release file signature does not exist, will download from {}", url );
            try ( InputStream is = url.openStream() )
            {
                Files.copy( is, signaturePath );
            }
        }
        else
        {
            LOGGER.debug( "Release file signature already exists, will not download" );
        }

        // download repository keyring
        keyringPath = Paths.get( buildDir, "archive-keyring.gpg" );
        if ( !Files.exists( keyringPath ) )
        {
            URL url = new URL( properties.getProperty( "test.keyring.url" ) );
            LOGGER.info( "Archive keyring does not exist, will download from {}", url );
            try ( InputStream is = url.openStream() )
            {
                Files.copy( is, keyringPath );
            }
        }
        else
        {
            LOGGER.debug( "Archive keyring already exists, will not download" );
        }
    }


    @Before
    public void setUp()
    {
        parser = new ReleaseIndexParserImpl();
    }


    @After
    public void tearDown()
    {
    }


    @Test
    public void testParse() throws Exception
    {
        ReleaseFile rf;
        try ( InputStream data = new FileInputStream( releaseFilePath.toFile() );
              InputStream sign = new FileInputStream( signaturePath.toFile() );
              InputStream key = new FileInputStream( keyringPath.toFile() ); )
        {
            rf = parser.parseWithSignature( data, sign, key );
        }

        Assert.assertNotNull( rf );
        Assert.assertTrue( rf.getIndices().size() > 0 );
        Assert.assertEquals( codename, rf.getCodename() );
    }


}

