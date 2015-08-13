package ai.subut.kurjun.storage.s3;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.model.storage.FileStore;


/**
 * Guice module to initialize file store bindings to S3 backed file store implementation.
 *
 */
public class S3FileStoreModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( FileStore.class ).to( S3FileStore.class );
    }

}

