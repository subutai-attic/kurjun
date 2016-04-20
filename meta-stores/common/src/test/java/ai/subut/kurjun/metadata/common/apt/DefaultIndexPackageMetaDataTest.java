package ai.subut.kurjun.metadata.common.apt;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import org.apache.commons.lang.RandomStringUtils;

import com.google.common.collect.Lists;

import ai.subut.kurjun.model.index.TagItem;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class DefaultIndexPackageMetaDataTest
{
    private DefaultIndexPackageMetaData packageMetaData;
    private List<TagItem> tag;



    @Before
    public void setUp() throws Exception
    {
        tag = Lists.newArrayList();

        packageMetaData = new DefaultIndexPackageMetaData();

        packageMetaData.setSha1( SHA1() );
        packageMetaData.setSha256( SHA256() );
        packageMetaData.setSize( 555555 );
        packageMetaData.setDescriptionMd5( md5() );
        packageMetaData.setTag( tag );

    }


    @Test
    public void getSHA1() throws Exception
    {
        assertNotNull( packageMetaData.getSHA1() );
    }


    @Test
    public void getSHA256() throws Exception
    {
        assertNotNull( packageMetaData.getSHA256() );
    }


    @Test
    public void getSize() throws Exception
    {
        assertNotNull( packageMetaData.getSize() );
    }


    @Test
    public void getDescriptionMd5() throws Exception
    {
        assertNotNull( packageMetaData.getDescriptionMd5() );
    }


    @Test
    public void getTag() throws Exception
    {
        assertNotNull( packageMetaData.getTag() );
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


    public static byte[] SHA1()
    {
        return hash( "SHA-1" );
    }


    public static byte[] SHA256()
    {
        return hash( "SHA-256" );
    }


    public static byte[] md5()
    {
        return hash( "MD5" );
    }
}