package ai.subut.kurjun.storage.fs;


import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.storage.FileStore;


/**
 * File system backed file store factory interface.
 *
 */
public interface FileSystemFileStoreFactory
{

    /**
     * Creates file system backed file store for the supplied context.
     *
     * @param context context
     * @return file system backed file store
     */
    FileStore create( KurjunContext context );

}

