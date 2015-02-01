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
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.apache.commons.codec.binary.Hex;


public class FileHelpersTest
{

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    private File file;
    private String checksumAlgorithm = "MD5";
    private String sampleData = "abcdefghijklmnopqrstuvwxyz";
    private String md5ofSampleData = "c3fcd3d76192e4007dfb496cca67e13b";


    @Before
    public void setUp() throws IOException
    {
        file = tempDir.newFile();
        try ( FileOutputStream os = new FileOutputStream( file ) )
        {
            os.write( sampleData.getBytes() );
        }
    }


    @After
    public void tearDown()
    {
    }


    @Test
    public void testChecksumOfFile() throws Exception
    {
        byte[] checksum = FileHelpers.checksum( file, checksumAlgorithm );
        Assert.assertEquals( md5ofSampleData, Hex.encodeHexString( checksum ) );
    }


    @Test
    public void testChecksumOfInputStream() throws Exception
    {
        byte[] checksum = FileHelpers.checksum( new FileInputStream( file ), checksumAlgorithm );
        Assert.assertEquals( md5ofSampleData, Hex.encodeHexString( checksum ) );
    }


    @Test( expected = NoSuchAlgorithmException.class )
    public void testChecksumWithInvalidAlgorithm() throws NoSuchAlgorithmException
    {
        FileHelpers.checksum( file, "MY-ALGO" );
    }


}

