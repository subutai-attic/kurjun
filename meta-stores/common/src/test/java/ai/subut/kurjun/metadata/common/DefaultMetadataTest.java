package ai.subut.kurjun.metadata.common;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import org.apache.commons.lang.RandomStringUtils;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class DefaultMetadataTest
{
    private DefaultMetadata defaultMetadata;


    @Before
    public void setUp() throws Exception
    {
        defaultMetadata = new DefaultMetadata();

        defaultMetadata.setFingerprint( "FCCF494471A9E89AB05C6BCED48E74E18333EBA3" );
        defaultMetadata.setMd5sum( md5() );
        defaultMetadata.setName( "meta" );
        defaultMetadata.setVersion( "1.0.0" );
        defaultMetadata.setSerialized( "serialized" );
    }


    @Test
    public void getFingerprint() throws Exception
    {
        assertNotNull( defaultMetadata.getFingerprint() );
    }


    @Test
    public void getId() throws Exception
    {
        assertNotNull( defaultMetadata.getId() );
    }


    @Test
    public void getMd5Sum() throws Exception
    {
        assertNotNull( defaultMetadata.getMd5Sum() );
    }


    @Test
    public void getName() throws Exception
    {
        assertNotNull( defaultMetadata.getName() );
    }


    @Test
    public void getVersion() throws Exception
    {
        assertNotNull( defaultMetadata.getVersion() );
    }


    @Test
    public void serialize() throws Exception
    {
        assertNotNull( defaultMetadata.serialize() );
    }


    @Test
    public void equals() throws Exception
    {
        defaultMetadata.equals( new Object() );
        defaultMetadata.equals( defaultMetadata );
        defaultMetadata.hashCode();
    }


    @Test
    public void idIsNull()
    {
        DefaultMetadata metadata = new DefaultMetadata();

        assertNull( metadata.getId() );
    }


    private static byte[] hash( String algo )
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance( algo );
            return digest.digest( RandomStringUtils.randomAscii( 100 ).getBytes() );
        }
        catch ( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
        }

        return new byte[0];
    }


    public static byte[] md5()
    {
        return hash( "MD5" );
    }
}