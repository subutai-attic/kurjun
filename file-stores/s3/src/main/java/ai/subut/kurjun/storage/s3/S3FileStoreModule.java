package ai.subut.kurjun.storage.s3;


import com.amazonaws.auth.AWSCredentials;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import ai.subut.kurjun.model.storage.FileStore;


/**
 * Guice module to initialize file store bindings to S3 backed file store implementation.
 *
 */
public class S3FileStoreModule extends AbstractModule
{

    /**
     * Properties key for Amazon S3 bucket name.
     */
    public static final String BUCKET_NAME = "file.store.s3.bucketName";

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

        bind( AWSCredentials.class ).toProvider( AWSCredentialsProvider.class );
    }


}

