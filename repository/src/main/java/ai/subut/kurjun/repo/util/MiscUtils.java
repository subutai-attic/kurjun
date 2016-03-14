package ai.subut.kurjun.repo.util;


import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;


public class MiscUtils
{
    /**
     * Calculates the md5 checksum of the given input stream
     *
     * @return md5 checksum, or <code>null</code> if exception occurred
     */
    public static byte[] calculateMd5( InputStream is )
    {
        byte[] md5 = null;
        try
        {
            md5 = DigestUtils.md5( is );
        }
        catch ( IOException e )
        {
        }
        return md5;
    }


}
