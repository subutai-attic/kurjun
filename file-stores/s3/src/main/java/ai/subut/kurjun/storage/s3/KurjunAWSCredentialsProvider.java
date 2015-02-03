package ai.subut.kurjun.storage.s3;


import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;


public class KurjunAWSCredentialsProvider implements AWSCredentialsProvider
{
    private static final Logger LOGGER = LoggerFactory.getLogger( KurjunAWSCredentialsProvider.class );


    @Override
    public AWSCredentials getCredentials()
    {
        try
        {
            InputStream is = getClass().getClassLoader().getResourceAsStream( "kurjun-aws.properties" );
            return new PropertiesCredentials( is );
        }
        catch ( IOException ex )
        {
            LOGGER.error( "Failed to read properties file", ex );
        }
        catch ( IllegalArgumentException ex )
        {
            LOGGER.error( "Invalid properties file", ex );
        }
        return null;
    }


    @Override
    public void refresh()
    {
        // no-op. static credentials are used
    }

}

