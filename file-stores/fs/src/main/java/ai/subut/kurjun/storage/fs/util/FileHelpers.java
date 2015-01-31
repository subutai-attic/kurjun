/*
 * Copyright 2015 azilet.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

