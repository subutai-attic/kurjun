package ai.subut.kurjun.storage.factory;


import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.model.storage.FileStore;


/**
 * Common file store factory for all available implementations of file stores.
 *
 */
public interface FileStoreFactory
{

    /**
     * Property key for file store type.
     */
    String TYPE = "file.storage.type";

    /**
     * File system backed file store type key.
     */
    String FILE_SYSTEM = "fs";

    /**
     * Amazon S3 backed file store type key.
     */
    String S3 = "s3";


    /**
     * Creates file store for the supplied context. File store type is identified by
     * {@link FileStoreModule#FILE_STORE_TYPE} key.
     *
     * @param context
     * @return
     */
    FileStore create( KurjunContext context );

}

