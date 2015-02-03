package ai.subut.kurjun.storage.fs.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileHelpers
{
    private static final Logger LOGGER = LoggerFactory.getLogger( FileHelpers.class );


    private FileHelpers()
    {
    }


    public static byte[] checksum( File file, String algorithm ) throws NoSuchAlgorithmException
    {
        try ( FileInputStream fis = new FileInputStream( file ) )
        {
            return checksum( fis, algorithm );
        }
        catch ( IOException ex )
        {
            LOGGER.error( "File not found", ex );
            return null;
        }
    }


    public static byte[] checksum( InputStream is, String algorithm ) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance( algorithm );
        int len;
        byte[] buf = new byte[1024];
        try
        {
            while ( ( len = is.read( buf ) ) != -1 )
            {
                md.update( buf, 0, len );
            }
            return md.digest();
        }
        catch ( IOException ex )
        {
            LOGGER.error( "Failed to read input stream", ex );
            return null;
        }
    }


    public static void close( AutoCloseable closeable )
    {
        if ( closeable != null )
        {
            try
            {
                closeable.close();
            }
            catch ( Exception ex )
            {
                LOGGER.warn( "Failed to close resource", ex );
            }
        }
    }
}

