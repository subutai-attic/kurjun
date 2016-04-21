package ai.subut.kurjun.security.manager.utils.ssl;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class RestUtilsTest
{
    private RestUtils restUtils;


    @Before
    public void setUp() throws Exception
    {
        restUtils = new RestUtils();
    }


    @Test
    public void getClientTrustManagers() throws Exception
    {
        restUtils.getClientTrustManagers();
    }


    @Test
    public void getClientFullTrustManagers() throws Exception
    {
        restUtils.getClientFullTrustManagers();
    }


    @Test
    public void createWebClient() throws Exception
    {
        assertNotNull( restUtils.createWebClient( "google.com", 5, 5, 3 ) );
    }


    @Test
    public void createTrustedWebClient() throws Exception
    {
        assertNotNull( restUtils.createTrustedWebClient( "google.com" ) );
    }


    @Test
    public void closeClient() throws Exception
    {
        restUtils.closeClient( restUtils.createTrustedWebClient( "google.com" ) );

        RestUtils.RequestType.GET.toString();
    }
}