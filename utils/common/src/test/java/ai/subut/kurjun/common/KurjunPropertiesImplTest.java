package ai.subut.kurjun.common;


import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import com.google.inject.Binder;

import ai.subut.kurjun.common.service.KurjunContext;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class KurjunPropertiesImplTest
{
    private KurjunPropertiesImpl kurjunProperties;

    @Mock
    Properties properties;

    @Mock
    KurjunContext kurjunContext;

    @Mock
    Binder binder;


    @Before
    public void setUp() throws Exception
    {
        kurjunProperties = new KurjunPropertiesImpl();
    }


    @Test
    public void get() throws Exception
    {
        kurjunProperties.get( "test" );
    }


    @Test
    public void getWithDefault() throws Exception
    {
        kurjunProperties.getWithDefault( "test", "test" );
    }


    @Test
    public void getInteger() throws Exception
    {
        kurjunProperties.getInteger( "test" );
    }


    @Test
    public void getIntegerWithDefault() throws Exception
    {
        kurjunProperties.getIntegerWithDefault( "test", 1 );
    }


    @Test
    public void getBoolean() throws Exception
    {
        kurjunProperties.getBoolean( "test" );
    }


    @Test
    public void getBooleanWithDefault() throws Exception
    {
        kurjunProperties.getBooleanWithDefault( "test", true );
    }


    @Test
    public void propertyMap() throws Exception
    {
        kurjunProperties.propertyMap();
    }


    @Test
    public void getContextProperties() throws Exception
    {
        kurjunProperties.getContextProperties( "test" );
    }


    @Test
    public void getContextProperties1() throws Exception
    {
        when( kurjunContext.getName() ).thenReturn( "test" );

        kurjunProperties.getContextProperties( kurjunContext );
    }
}