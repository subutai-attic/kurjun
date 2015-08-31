package ai.subut.kurjun.storage.factory;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.storage.fs.FileSystemFileStoreModule;
import ai.subut.kurjun.storage.s3.S3FileStoreModule;


/**
 * Guice module that binds common file store factory class.
 *
 */
public class FileStoreModule extends AbstractModule
{

    /**
     * Property key for file store type.
     */
    public static final String FILE_STORE_TYPE = "file.storage.type";


    @Override
    protected void configure()
    {
        // install all modules of file store implementations so that we have impl specific factories bound
        install( new FileSystemFileStoreModule() );
        install( new S3FileStoreModule() );

        bind( FileStoreFactory.class );
    }

}

