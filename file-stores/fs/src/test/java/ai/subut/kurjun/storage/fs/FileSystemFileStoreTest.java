package ai.subut.kurjun.storage.fs;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.apache.commons.codec.digest.DigestUtils;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class FileSystemFileStoreTest
{
    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    public final String MD5 = "MD5";
    public static final String FILE_STORE_FS_DIR_PATH = "file.store.fs.path";

    private FileSystemFileStore fs;
    private File sampleFile;
    private String sampleData = "sample data";
    private byte[] sampleMd5;

    @Mock
    KurjunProperties kurjunProperties;

    @Mock
    KurjunContext kurjunContext;

    @Mock
    Properties properties;


    @Before
    public void setUp() throws IOException
    {
        // mock
        when( kurjunProperties.getContextProperties( kurjunContext ) ).thenReturn( properties );
        when( kurjunContext.getName() ).thenReturn( "test" );

        sampleFile = tempDir.newFile();
        try ( OutputStream os = new FileOutputStream( sampleFile ) )
        {
            os.write( sampleData.getBytes( StandardCharsets.UTF_8 ) );
        }
        sampleMd5 = DigestUtils.md5( sampleData );

        fs = new FileSystemFileStore( tempDir.newFolder().getAbsolutePath() );


        fs = new FileSystemFileStore( kurjunProperties, kurjunContext );
        fs.put( sampleFile );
    }


    @After
    public void tearDown()
    {
    }


    @Test
    public void testFileSystemFileStore()
    {
    }


    @Test
    public void testContains() throws Exception
    {
        Assert.assertTrue( fs.contains( sampleMd5 ) );

        try ( InputStream is = new FileInputStream( tempDir.newFile() ) )
        {
            byte[] otherMd5 = DigestUtils.md5( is );
            Assert.assertFalse( fs.contains( otherMd5 ) );
        }
    }


    @Test
    public void testGet() throws Exception
    {
        assertNotNull( fs.get( sampleMd5 ) );
    }


    @Test
    public void testGetWithInvalidKey() throws IOException
    {
        byte[] checksum = DigestUtils.md5( "abc" );
        Assert.assertNull( fs.get( checksum ) );
    }


    @Test
    public void testGetWithTarget() throws Exception
    {
        assertNotNull( fs.get( sampleMd5, sampleFile ) );
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


    @Test
    public void testSizeOf() throws Exception
    {
        int expected = sampleData.getBytes().length;
        long sizeof = fs.sizeOf( sampleMd5 );
        Assert.assertEquals( expected, sizeof );
        Assert.assertEquals( 0, fs.sizeOf( DigestUtils.md5( "non-existing" ) ) );
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


    @Test
    public void testSize() throws IOException
    {
        assertNotNull( fs.size() );
    }
}

