package ai.subut.kurjun.metadata.storage.nosql;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.datastax.driver.core.exceptions.NoHostAvailableException;

import ai.subut.kurjun.common.service.KurjunProperties;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class CassandraSessionProviderTest
{
    public static final String CASSANDRA_NODE = "metadata.store.cassandra.node";
    public static final String CASSANDRA_PORT = "metadata.store.cassandra.port";

    private CassandraSessionProvider sessionProvider;

    @Mock
    KurjunProperties kurjunProperties;


    @Before
    public void setUp() throws Exception
    {
        sessionProvider = new CassandraSessionProvider( kurjunProperties );
    }


    @Test( expected = NoHostAvailableException.class )
    public void getExeption() throws Exception
    {
        // mock
        when( kurjunProperties.get( CASSANDRA_NODE ) ).thenReturn( "192.168.0.106" );
        when( kurjunProperties.getIntegerWithDefault( anyString(), anyInt() ) ).thenReturn( 2 );

        sessionProvider.get();
    }


    @Test( expected = NoHostAvailableException.class )
    public void getExeption2() throws Exception
    {
        // mock
        when( kurjunProperties.get( CASSANDRA_NODE ) ).thenReturn( null );

        sessionProvider.get();
    }


    @Test
    public void close() throws Exception
    {
        sessionProvider.close();
    }
}