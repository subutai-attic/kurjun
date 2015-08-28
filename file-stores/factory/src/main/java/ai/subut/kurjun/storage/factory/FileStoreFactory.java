package ai.subut.kurjun.storage.factory;


import com.google.inject.Inject;

import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.storage.fs.FileSystemFileStoreFactory;
import ai.subut.kurjun.storage.s3.S3FileStoreFactory;


/**
 * File store factory for all available implementations of file stores.
 *
 */
public class FileStoreFactory
{

    @Inject
    private FileSystemFileStoreFactory fileSystemFileStoreFactory;

    @Inject
    private S3FileStoreFactory s3FileStoreFactory;


    /**
     * Wrapper to {@link FileSystemFileStoreFactory#create(java.lang.String) }
     *
     * @param parentDirectory
     * @return
     */
    public FileStore createFileSystemFileStore( String parentDirectory )
    {
        return fileSystemFileStoreFactory.create( parentDirectory );
    }


    /**
     * Wrapper to {@link  S3FileStoreFactory#create(java.lang.String)}
     *
     * @param bucketName
     * @return
     */
    public FileStore createS3FileStore( String bucketName )
    {
        return s3FileStoreFactory.create( bucketName );
    }
}

