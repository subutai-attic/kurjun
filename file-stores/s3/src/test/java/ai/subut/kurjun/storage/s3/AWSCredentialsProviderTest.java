package ai.subut.kurjun.storage.s3;


import java.security.ProviderException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import org.apache.commons.io.output.StringBuilderWriter;

import com.google.inject.ProvisionException;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class AWSCredentialsProviderTest
{
    public static final String SECRET_KEY = "secretKey";
    public static final String ACCESS_KEY = "accessKey";
    private AWSCredentialsProvider credentialsProvider;


    @Before
    public void setUp() throws Exception
    {
        credentialsProvider = new AWSCredentialsProvider();
    }


    @Test( expected = ProvisionException.class )
    public void setAccessKey() throws Exception
    {
        credentialsProvider.setSecretKey( SECRET_KEY );

        assertNull( credentialsProvider.get() );
    }


    @Test( expected = ProvisionException.class )
    public void setSecretKey() throws Exception
    {
        credentialsProvider.setAccessKey( ACCESS_KEY );

        assertNull( credentialsProvider.get() );
    }


    @Test
    public void get() throws Exception
    {
        credentialsProvider.setAccessKey( ACCESS_KEY );
        credentialsProvider.setSecretKey( SECRET_KEY );

        assertNotNull( credentialsProvider.get() );
    }
}