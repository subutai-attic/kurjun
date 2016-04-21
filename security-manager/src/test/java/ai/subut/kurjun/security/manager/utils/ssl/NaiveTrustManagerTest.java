package ai.subut.kurjun.security.manager.utils.ssl;


import java.security.cert.X509Certificate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith( MockitoJUnitRunner.class )
public class NaiveTrustManagerTest
{
    private NaiveTrustManager naiveTrustManager;

    @Before
    public void setUp() throws Exception
    {
        naiveTrustManager = new NaiveTrustManager();
    }


    @Test
    public void checkClientTrusted() throws Exception
    {
        X509Certificate[] certificates = null;
        naiveTrustManager.checkClientTrusted( certificates, "authType" );
    }


    @Test
    public void checkServerTrusted() throws Exception
    {
        X509Certificate[] certificates = null;
        naiveTrustManager.checkServerTrusted( certificates, "authType" );
    }


    @Test
    public void getAcceptedIssuers() throws Exception
    {
        naiveTrustManager.getAcceptedIssuers();
    }
}