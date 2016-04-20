package ai.subut.kurjun.metadata.common.raw;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import ai.subut.kurjun.metadata.common.apt.DefaultIndexPackageMetaDataTest;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class RawMetadataTest
{
    private RawMetadata rawMetadata;


    @Before
    public void setUp() throws Exception
    {
        rawMetadata = new RawMetadata( DefaultIndexPackageMetaDataTest.md5(), "name", 5,
                "FCCF494471A9E89AB05C6BCED48E74E18333EBA3" );

        rawMetadata.setFingerprint( "FCCF494471A9E89AB05C6BCED48E74E18333EBA3" );
        rawMetadata.setName( "name" );
        rawMetadata.serialize();
        rawMetadata.setMd5Sum( DefaultIndexPackageMetaDataTest.md5() );
        rawMetadata.setSize( 5 );
    }


    @Test
    public void getSize() throws Exception
    {
        assertNotNull( rawMetadata.getSize() );
    }


    @Test
    public void getId() throws Exception
    {
        assertNotNull( rawMetadata.getId() );
    }


    @Test
    public void getMd5Sum() throws Exception
    {
        assertNotNull( rawMetadata.getMd5Sum() );
    }


    @Test
    public void getName() throws Exception
    {
        assertNotNull( rawMetadata.getName() );
    }


    @Test
    public void getVersion() throws Exception
    {
        assertNotNull( rawMetadata.getVersion() );
    }


    @Test
    public void getFingerprint() throws Exception
    {
        assertNotNull( rawMetadata.getFingerprint() );
    }


    @Test
    public void equals() throws Exception
    {
        rawMetadata = new RawMetadata();

        // assets
        assertNull( rawMetadata.getId() );
        rawMetadata.hashCode();
        rawMetadata.equals( new Object() );
        rawMetadata.equals( rawMetadata );
    }
}