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
package ai.subut.kurjun.storage.s3;


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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;


public class S3FileStoreTest
{
    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    private static S3FileStore s3;

    private File sampleFile;
    private String sampleData = "sample data";
    private byte[] sampleMd5;


    @BeforeClass
    public static void setUpClass() throws Exception
    {
        s3 = new S3FileStore( UUID.randomUUID().toString() );
    }


    @AfterClass
    public static void tearDownClass() throws Exception
    {
        // delete all objects
        ObjectListing listing = s3.s3client.listObjects( s3.bucketName );
        for ( S3ObjectSummary obj : listing.getObjectSummaries() )
        {
            s3.s3client.deleteObject( s3.bucketName, obj.getKey() );
        }
        // delete bucket
        s3.s3client.deleteBucket( s3.bucketName );
    }


    @Before
    public void setUp() throws IOException, NoSuchAlgorithmException
    {
        sampleFile = tempDir.newFile();
        try ( OutputStream os = new FileOutputStream( sampleFile ) )
        {
            os.write( sampleData.getBytes( StandardCharsets.UTF_8 ) );
        }
        sampleMd5 = s3.put( sampleFile );
    }


    @After
    public void tearDown() throws IOException
    {
    }


    @Test
    public void testContains() throws Exception
    {
        Assert.assertTrue( s3.contains( sampleMd5 ) );

        byte[] otherMd5 = checksum( new ByteArrayInputStream( "12345".getBytes( StandardCharsets.UTF_8 ) ) );
        Assert.assertFalse( s3.contains( otherMd5 ) );
    }


    @Test
    public void testGet() throws Exception
    {
        try ( InputStream is = s3.get( sampleMd5 ) )
        {
            Assert.assertNotNull( is );
            Assert.assertEquals( sampleData, readAsString( is ) );
        }
    }


    @Test
    public void testGetWithInvalidKey() throws IOException, NoSuchAlgorithmException
    {
        byte[] checksum = checksum( new ByteArrayInputStream( "abc".getBytes() ) );
        Assert.assertNull( s3.get( checksum ) );
    }


    @Test
    public void testGetWithTarget() throws Exception
    {
        File target = tempDir.newFile();
        Assert.assertTrue( s3.get( sampleMd5, target ) );

        try ( InputStream is = new FileInputStream( target ) )
        {
            String contents = readAsString( is );
            Assert.assertEquals( sampleData, contents );
        }

        // with invalid key
        byte[] checksum = checksum( new ByteArrayInputStream( "abc".getBytes() ) );
        Assert.assertFalse( s3.get( checksum, target ) );
    }


    @Test
    public void testPut_File() throws Exception
    {
        byte[] checksum = s3.put( sampleFile );
        Assert.assertArrayEquals( sampleMd5, checksum );
        Assert.assertTrue( s3.contains( checksum ) );
    }


    @Test
    public void testPut_URL() throws Exception
    {
        byte[] checksum = s3.put( new URL( "http://example.com" ) );
        Assert.assertNotNull( checksum );
        Assert.assertTrue( s3.contains( checksum ) );
    }


    @Test( expected = IOException.class )
    public void testPutWithInvalidURL() throws Exception
    {
        s3.put( new URL( "with://inval.id/path" ) );
    }


    @Test
    public void testPutWithFilenameAndInputStream() throws Exception
    {
        byte[] checksum = s3.put( "my-filename", new FileInputStream( sampleFile ) );
        Assert.assertArrayEquals( sampleMd5, checksum );
        Assert.assertTrue( s3.contains( checksum ) );
    }


    @Test
    public void testRemove() throws Exception
    {
        Assert.assertTrue( s3.remove( sampleMd5 ) );
        Assert.assertFalse( s3.remove( sampleMd5 ) );
        Assert.assertFalse( s3.contains( sampleMd5 ) );
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


    private byte[] checksum( InputStream is ) throws NoSuchAlgorithmException, IOException
    {
        MessageDigest md = MessageDigest.getInstance( "MD5" );
        int len;
        byte[] buf = new byte[1024];
        while ( ( len = is.read( buf ) ) != -1 )
        {
            md.update( buf, 0, len );
        }
        return md.digest();
    }


}

