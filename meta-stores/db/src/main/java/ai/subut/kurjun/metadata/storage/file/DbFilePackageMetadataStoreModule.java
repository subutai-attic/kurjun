package ai.subut.kurjun.metadata.storage.file;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.model.metadata.PackageMetadataStore;


/**
 * Guice module to initialize metadata store bindings to file backed metadata store implementations.
 *
 */
public class DbFilePackageMetadataStoreModule extends AbstractModule
{

    public static final String DB_FILE_LOCATION_NAME = "metadata.storage.file.location";


    @Override
    protected void configure()
    {
        bind( PackageMetadataStore.class ).to( DbFilePackageMetadataStore.class );
    }

}

