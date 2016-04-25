package ai.subut.kurjun.security.manager.utils.ssl;


import java.security.KeyStore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class SSLManagerTest
{
    private SSLManager sslManager;

    @Mock
    KeyStore store;


    @Before
    public void setUp() throws Exception
    {
        sslManager = new SSLManager( store, store );
    }


    @Test
    public void getClientKeyManagers() throws Exception
    {
        sslManager.getClientKeyManagers( "test" );
    }


    @Test
    public void getClientTrustManagers() throws Exception
    {
        sslManager.getClientTrustManagers( "test" );
    }


    @Test
    public void getClientFullTrustManagers() throws Exception
    {
        assertNotNull( sslManager.getClientFullTrustManagers() );
    }
}