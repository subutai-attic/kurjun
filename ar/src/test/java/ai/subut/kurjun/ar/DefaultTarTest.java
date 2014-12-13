package ai.subut.kurjun.ar;


import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests the default Tar implementation.
 */
public class DefaultTarTest
{
    private static final Logger LOG = LoggerFactory.getLogger( DefaultTarTest.class );
    public static final String[] TARBALLS = {
            "test.tar", "test.tar.gz", "test.tar.xz", "test.tar.bz2", "test.tar.lzma"
    };


    @Test
    public void testExtract() throws IOException
    {
        for ( String ar : TARBALLS )
        {
            URL url = ClassLoader.getSystemResource( ar );
            File tarFile = new File( url.getFile() );
            DefaultTar tar = new DefaultTar( tarFile );
            tar.extract( tarFile.getParentFile() );
        }
    }
}
