package ai.subut.kurjun.common.utils;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class InetUtilsTest
{
    private InetUtils inetUtils;


    @Before
    public void setUp() throws Exception
    {
    }


    @Test
    public void getLocalIPAddresses() throws Exception
    {
        assertNotNull( InetUtils.getLocalIPAddresses() );
        ;
    }


    @Test
    public void isHostReachable() throws Exception
    {
        assertFalse( InetUtils.isHostReachable( "localhost", 8080, 5 ) );
    }
}