package ai.subut.kurjun.common;


import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.commons.configuration.ConfigurationException;


public class KurjunPropertiesImplTest
{

    private KurjunPropertiesImpl properties;


    @Before
    public void setUp() throws ConfigurationException
    {
        properties = new KurjunPropertiesImpl();
    }


    @After
    public void tearDown() throws Exception
    {
    }


    @Test
    public void testGet()
    {
        Assert.assertEquals( "today is sunny", properties.get( "string.value" ) );
        Assert.assertNull( properties.get( "invalid.key" ) );
    }


    @Test
    public void testGetWithDefault()
    {
        Assert.assertEquals( "today is funny", properties.getWithDefault( "invalid.key", "today is funny" ) );
    }


    @Test
    @SuppressWarnings( "UnnecessaryBoxing" )
    public void testGetInteger()
    {
        Assert.assertEquals( Integer.valueOf( 1234 ), properties.getInteger( "int.value" ) );
        Assert.assertNull( properties.getInteger( "int.value.invalid" ) );
        Assert.assertNull( properties.getInteger( "invalid.key" ) );
    }


    @Test
    @SuppressWarnings( "UnnecessaryBoxing" )
    public void testGetIntegerWithDefault()
    {
        Assert.assertEquals( Integer.valueOf( 2233 ), properties.getIntegerWithDefault( "invalid.key", 2233 ) );
        Assert.assertEquals( Integer.valueOf( 2233 ), properties.getIntegerWithDefault( "int.value.invalid", 2233 ) );
    }


    @Test
    @SuppressWarnings( "AssertEqualsBetweenInconvertibleTypes" )
    public void testGetBoolean()
    {
        Assert.assertEquals( true, properties.getBoolean( "bool.value.1" ) );
        Assert.assertEquals( true, properties.getBoolean( "bool.value.2" ) );
        Assert.assertNull( properties.getBoolean( "bool.value.3" ) );
        Assert.assertNull( properties.getBoolean( "bool.value.invalid" ) );
        Assert.assertNull( properties.getBoolean( "invalid.key" ) );
    }


    @Test
    @SuppressWarnings( "AssertEqualsBetweenInconvertibleTypes" )
    public void testGetBooleanWithDefault()
    {
        Assert.assertEquals( false, properties.getBooleanWithDefault( "invalid.key", false ) );
        Assert.assertEquals( false, properties.getBooleanWithDefault( "int.value.invalid", false ) );
    }


    @Test
    public void testGetContextProperties()
    {
        String context = "some-context";
        Properties p = properties.getContextProperties( context );
        Assert.assertNotNull( p );

        p.put( "key", "value" );
        p = properties.getContextProperties( context );
        Assert.assertEquals( "value", p.getProperty( "key" ) );
    }

}

