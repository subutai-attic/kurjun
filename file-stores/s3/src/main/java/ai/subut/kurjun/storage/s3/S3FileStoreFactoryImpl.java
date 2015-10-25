package ai.subut.kurjun.storage.s3;


import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.model.storage.FileStore;


/**
 * Amazon S3 backed file store factory. Used for OSGi DI.
 *
 */
class S3FileStoreFactoryImpl implements S3FileStoreFactory
{
    private KurjunProperties properties;


    public S3FileStoreFactoryImpl( KurjunProperties properties )
    {
        this.properties = properties;
    }


    @Override
    public FileStore create( KurjunContext context )
    {
        AWSCredentialsProvider credentialsProvider = new AWSCredentialsProvider();
        credentialsProvider.setAccessKey( properties.get( S3FileStoreModule.S3_ACCESS_KEY ) );
        credentialsProvider.setSecretKey( properties.get( S3FileStoreModule.S3_SECRET_KEY ) );

        return new S3FileStore( credentialsProvider.get(), properties, context );
    }

}

