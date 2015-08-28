package ai.subut.kurjun.storage.fs;


import ai.subut.kurjun.model.storage.FileStore;


/**
 * File system backed file store factory interface.
 *
 */
public interface FileSystemFileStoreFactory
{

    /**
     * Creates file system backed file store at the specified directory of local file system.
     *
     * @param parentDirectory directory where files will be stored
     * @return file system backed file store
     */
    FileStore create( String parentDirectory );

}

