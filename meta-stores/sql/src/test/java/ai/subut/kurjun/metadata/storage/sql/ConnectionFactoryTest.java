package ai.subut.kurjun.metadata.storage.sql;


import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ai.subut.kurjun.common.service.KurjunProperties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class ConnectionFactoryTest
{
    private ConnectionFactory connectionFactory;

    @Mock
    KurjunProperties kurjunProperties;


    @Before
    public void setUp() throws Exception
    {
        connectionFactory = ConnectionFactory.getInstance();
    }


    @Test
    public void getInstance() throws Exception
    {
        assertNotNull( ConnectionFactory.getInstance() );
    }


    @Test( expected = RuntimeException.class )
    public void init() throws Exception
    {
        // mock
        Map<String, Object> map = new HashMap();
        map.put( "dataSourceClassName", new Object() );

        when( kurjunProperties.propertyMap() ).thenReturn( map );

        connectionFactory.init( kurjunProperties );
    }


    @Test( expected = NullPointerException.class )
    public void getConnection() throws Exception
    {
        assertNull( connectionFactory.getConnection() );
    }


    @Test
    public void close() throws Exception
    {
        connectionFactory.close();
    }
}