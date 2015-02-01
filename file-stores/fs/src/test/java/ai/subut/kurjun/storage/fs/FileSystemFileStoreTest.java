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
package ai.subut.kurjun.storage.fs;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ai.subut.kurjun.storage.fs.util.FileHelpers;


public class FileSystemFileStoreTest
{
    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    public final String MD5 = "MD5";

    private FileSystemFileStore fs;
    private File sampleFile;
    private String sampleData = "sample data";
    private byte[] sampleMd5;


    @Before
    public void setUp() throws IOException, NoSuchAlgorithmException
    {
        sampleFile = tempDir.newFile();
        try ( OutputStream os = new FileOutputStream( sampleFile ) )
        {
            os.write( sampleData.getBytes( StandardCharsets.UTF_8 ) );
        }
        sampleMd5 = FileHelpers.checksum( sampleFile, MD5 );

        fs = new FileSystemFileStore( tempDir.newFolder().getAbsolutePath() );
        fs.put( sampleFile );
    }


    @After
    public void tearDown()
    {
    }


    @Test
    public void testContains() throws Exception
    {
        Assert.assertTrue( fs.contains( sampleMd5 ) );

        byte[] otherMd5 = FileHelpers.checksum( tempDir.newFile(), MD5 );
        Assert.assertFalse( fs.contains( otherMd5 ) );
    }


    @Test
    public void testGet() throws Exception
    {
        try ( InputStream is = fs.get( sampleMd5 ) )
        {
            Assert.assertNotNull( is );
            Assert.assertEquals( sampleData, readAsString( is ) );
        }
    }


    @Test
    public void testGetWithInvalidKey() throws IOException, NoSuchAlgorithmException
    {
        byte[] checksum = FileHelpers.checksum( new ByteArrayInputStream( "abc".getBytes() ), MD5 );
        Assert.assertNull( fs.get( checksum ) );
    }


    @Test
    public void testGetWithTarget() throws Exception
    {
        File target = tempDir.newFile();
        Assert.assertTrue( fs.get( sampleMd5, target ) );

        try ( InputStream is = new FileInputStream( target ) )
        {
            String contents = readAsString( is );
            Assert.assertEquals( sampleData, contents );
        }

        // with invalid key
        byte[] checksum = FileHelpers.checksum( new ByteArrayInputStream( "abc".getBytes() ), MD5 );
        Assert.assertFalse( fs.get( checksum, target ) );
    }


    @Test
    public void testPut_File() throws Exception
    {
        byte[] checksum = fs.put( sampleFile );
        Assert.assertArrayEquals( sampleMd5, checksum );
        Assert.assertTrue( fs.contains( checksum ) );
    }


    @Test
    public void testPut_URL() throws Exception
    {
        byte[] checksum = fs.put( new URL( "http://example.com" ) );
        Assert.assertNotNull( checksum );
        Assert.assertTrue( fs.contains( checksum ) );
    }


    @Test( expected = IOException.class )
    public void testPutWithInvalidURL() throws Exception
    {
        fs.put( new URL( "with://inval.id/path" ) );
    }


    @Test
    public void testPutWithFilenameAndInputStream() throws Exception
    {
        byte[] checksum = fs.put( "my-filename", new FileInputStream( sampleFile ) );
        Assert.assertArrayEquals( sampleMd5, checksum );
        Assert.assertTrue( fs.contains( checksum ) );
    }


    @Test
    public void testRemove() throws Exception
    {
        Assert.assertTrue( fs.remove( sampleMd5 ) );
        Assert.assertFalse( fs.remove( sampleMd5 ) );
        Assert.assertFalse( fs.contains( sampleMd5 ) );
    }


    private String readAsString( InputStream is ) throws IOException
    {
        try ( ByteArrayOutputStream os = new ByteArrayOutputStream() )
        {
            int len;
            byte[] buf = new byte[1024];
            while ( ( len = is.read( buf ) ) != -1 )
            {
                os.write( buf, 0, len );
            }
            return os.toString( StandardCharsets.UTF_8.name() );
        }
    }

}

