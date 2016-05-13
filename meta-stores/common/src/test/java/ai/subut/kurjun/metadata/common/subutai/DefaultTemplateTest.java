package ai.subut.kurjun.metadata.common.subutai;


import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ai.subut.kurjun.metadata.common.apt.DefaultIndexPackageMetaDataTest;
import ai.subut.kurjun.model.metadata.Architecture;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class DefaultTemplateTest
{
    private DefaultTemplate defaultTemplate;
    private Map<String, String> extra = new HashMap<>();


    @Before
    public void setUp() throws Exception
    {
        defaultTemplate = new DefaultTemplate();

        defaultTemplate.setId( "FCCF494471A9E89AB05C6BCED48E74E18333EBA3", new String( DefaultIndexPackageMetaDataTest.md5() ) );
        defaultTemplate.setName( "name" );
        defaultTemplate.setVersion( "1.0.0" );
        defaultTemplate.setMd5Sum( DefaultIndexPackageMetaDataTest.md5() );
        defaultTemplate.setArchitecture( Architecture.ALL );
        defaultTemplate.setConfigContents( "configContent" );
        defaultTemplate.setOwnerFprint( "FCCF494471A9E89AB05C6BCED48E74E18333EBA3" );
        defaultTemplate.setPackage( "package" );
        defaultTemplate.setParent( "parent" );
        defaultTemplate.setSize( 5 );
        defaultTemplate.setExtra( extra );
        defaultTemplate.setPackagesContents( "packageContent" );
    }


    @Test
    public void getId() throws Exception
    {
        assertNotNull( defaultTemplate.getId() );
    }


    @Test
    public void getMd5Sum() throws Exception
    {
        assertNotNull( defaultTemplate.getMd5Sum() );
    }


    @Test
    public void getName() throws Exception
    {
        assertNotNull( defaultTemplate.getName() );
    }


    @Test
    public void getVersion() throws Exception
    {
        assertNotNull( defaultTemplate.getVersion() );
    }


    @Test
    public void getArchitecture() throws Exception
    {
        assertNotNull( defaultTemplate.getArchitecture() );
    }


    @Test
    public void getParent() throws Exception
    {
        assertNotNull( defaultTemplate.getParent() );
    }


    @Test
    public void getPackage() throws Exception
    {
        assertNotNull( defaultTemplate.getPackage() );
    }


    @Test
    public void getConfigContents() throws Exception
    {
        assertNotNull( defaultTemplate.getConfigContents() );
    }


    @Test
    public void getPackagesContents() throws Exception
    {
        assertNotNull( defaultTemplate.getPackagesContents() );
    }


    @Test
    public void getOwnerFprint() throws Exception
    {
        assertNotNull( defaultTemplate.getOwnerFprint() );
    }


    @Test
    public void getExtra() throws Exception
    {
        assertNotNull( defaultTemplate.getExtra() );
    }


    @Test
    public void getSize() throws Exception
    {
        assertNotNull( defaultTemplate.getSize() );
    }


    @Test
    public void serialize() throws Exception
    {
        defaultTemplate.serialize();
    }


    @Test
    public void equals() throws Exception
    {
        defaultTemplate.equals( new Object() );
        defaultTemplate.equals( defaultTemplate );
        defaultTemplate.hashCode();
    }


    @Test
    public void idIsNull()
    {
        defaultTemplate = new DefaultTemplate();

        assertNull( defaultTemplate.getId() );
    }
}