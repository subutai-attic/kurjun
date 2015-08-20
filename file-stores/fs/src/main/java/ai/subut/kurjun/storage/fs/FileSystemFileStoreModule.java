package ai.subut.kurjun.storage.fs;


import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import ai.subut.kurjun.model.storage.FileStore;


/**
 * Guice module to initialize file store bindings to file system backed store implementations.
 *
 */
public class FileSystemFileStoreModule extends AbstractModule
{

    public static final String ROOT_DIRECTORY = "storage.fs.root.directory";

    private String rootLocation;


    public String getRootLocation()
    {
        return rootLocation;
    }


    /**
     * Sets root location of the file store. Basically, this binds corresponding root location string to supplied
     * string. If not set injector will inject the value bound elsewhere.
     *
     * @param rootLocation root location in file system
     * @return
     */
    public FileSystemFileStoreModule setRootLocation( String rootLocation )
    {
        this.rootLocation = rootLocation;
        return this;
    }


    @Override
    protected void configure()
    {
        if ( rootLocation != null )
        {
            bind( String.class ).annotatedWith( Names.named( ROOT_DIRECTORY ) ).toInstance( rootLocation );
        }
        bind( FileStore.class ).to( FileSystemFileStore.class );
    }

}

