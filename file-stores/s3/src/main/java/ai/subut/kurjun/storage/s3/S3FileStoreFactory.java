package ai.subut.kurjun.storage.s3;


import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.model.storage.FileStore;


/**
 * Factory interface for Amazon S3 backed file store.
 *
 */
public interface S3FileStoreFactory
{

    /**
     * Create Amazon S3 backed file store for the supplied context.
     *
     * @param context context
     * @return file store
     */
    FileStore create( KurjunContext context );

}

