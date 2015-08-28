package ai.subut.kurjun.storage.factory;


import com.google.inject.AbstractModule;


/**
 * Guice module that binds common file store factory class.
 *
 */
public class FileStoreModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( FileStoreFactory.class );
    }

}

