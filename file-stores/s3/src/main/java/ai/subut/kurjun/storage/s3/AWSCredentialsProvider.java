package ai.subut.kurjun.storage.s3;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.name.Named;

import static ai.subut.kurjun.storage.s3.S3FileStoreModule.S3_ACCESS_KEY;
import static ai.subut.kurjun.storage.s3.S3FileStoreModule.S3_SECRET_KEY;


/**
 * Provider class for AWS credentials. Access and secret keys are injected from properties file.
 *
 */
class AWSCredentialsProvider implements Provider<AWSCredentials>
{

    private String accessKey;
    private String secretKey;


    @Inject( optional = true )
    public void setAccessKey( @Named( S3_ACCESS_KEY ) String accessKey )
    {
        this.accessKey = accessKey;
    }


    @Inject( optional = true )
    public void setSecretKey( @Named( S3_SECRET_KEY ) String secretKey )
    {
        this.secretKey = secretKey;
    }


    @Override
    public AWSCredentials get()
    {
        if ( accessKey == null )
        {
            throw new ProvisionException( "No value for property " + S3_ACCESS_KEY );
        }
        if ( secretKey == null )
        {
            throw new ProvisionException( "No value for property " + S3_SECRET_KEY );
        }
        return new BasicAWSCredentials( accessKey, secretKey );
    }

}

