package ai.subut.kurjun.metadata.common.snap;


import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;

import ai.subut.kurjun.metadata.common.apt.DefaultIndexPackageMetaDataTest;
import ai.subut.kurjun.model.metadata.snap.Framework;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class DefaultSnapMetadataTest
{
    private DefaultSnapMetadata snapMetadata;

    @Mock
    Framework framework;


    @Before
    public void setUp() throws Exception
    {
        List<Framework> list = Lists.newArrayList();

        snapMetadata = new DefaultSnapMetadata();

        snapMetadata.setName( "name" );
        snapMetadata.setMd5Sum( DefaultIndexPackageMetaDataTest.md5() );
        snapMetadata.setVersion( "1.0.0" );
        snapMetadata.setSource( "source" );
        snapMetadata.setVendor( "vendor" );
        snapMetadata.setFrameworks( list );
    }


    @Test
    public void getId() throws Exception
    {
        assertNotNull( snapMetadata.getId() );
    }


    @Test
    public void getMd5Sum() throws Exception
    {
        assertNotNull( snapMetadata.getMd5Sum() );
    }


    @Test
    public void getName() throws Exception
    {
        assertNotNull( snapMetadata.getName() );
    }


    @Test
    public void getVersion() throws Exception
    {
        assertNotNull( snapMetadata.getVersion() );
    }


    @Test
    public void getVendor() throws Exception
    {
        assertNotNull( snapMetadata.getVendor() );
    }


    @Test
    public void getSource() throws Exception
    {
        assertNotNull( snapMetadata.getSource() );
    }


    @Test
    public void getFrameworks() throws Exception
    {
        snapMetadata.getFrameworks().isEmpty();
    }


    @Test
    public void serialize() throws Exception
    {
        snapMetadata.serialize();
    }
}