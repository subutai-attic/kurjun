package ai.subut.kurjun.storage.fs;


import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import ai.subut.kurjun.model.storage.FileStore;


/**
 * Guice module to initialize file store bindings to file system backed store implementations.
 *
 */
public class FileSystemFileStoreModule extends AbstractModule
{
    public static final String TYPE = "fs";
    public static final String ROOT_DIRECTORY = "storage.fs.root.directory";


    @Override
    protected void configure()
    {
        Module module = new FactoryModuleBuilder()
                .implement( FileStore.class, FileSystemFileStore.class )
                .build( FileSystemFileStoreFactory.class );

        install( module );
    }

}

