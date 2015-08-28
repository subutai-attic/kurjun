package ai.subut.kurjun.storage.s3;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;

import ai.subut.kurjun.model.storage.FileStore;


/**
 * Guice module to initialize file store bindings to S3 backed file store implementation.
 *
 */
public class S3FileStoreModule extends AbstractModule
{

    /**
     * Properties key for Amazon S3 access key.
     */
    public static final String S3_ACCESS_KEY = "file.store.s3.accessKey";

    /**
     * Properties key for Amazon S3 secret key.
     */
    public static final String S3_SECRET_KEY = "file.store.s3.secretKey";


    @Override
    protected void configure()
    {
        Module module = new FactoryModuleBuilder()
                .implement( FileStore.class, S3FileStore.class )
                .build( S3FileStoreFactory.class );

        install( module );
    }


    @Provides
    AWSCredentials getAWSCredentials( @Named( S3_ACCESS_KEY ) String accessKey, @Named( S3_SECRET_KEY ) String secretKey )
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

