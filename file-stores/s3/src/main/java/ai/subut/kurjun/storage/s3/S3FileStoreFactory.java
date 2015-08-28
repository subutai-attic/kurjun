package ai.subut.kurjun.storage.s3;


import ai.subut.kurjun.model.storage.FileStore;


/**
 * Factory interface for Amazon S3 backed file store.
 *
 */
public interface S3FileStoreFactory
{

    /**
     * Create Amazon S3 backed file store with supplied bucket name.
     *
     * @param bucketName backing bucket name
     * @return file store
     */
    FileStore create( String bucketName );

}

