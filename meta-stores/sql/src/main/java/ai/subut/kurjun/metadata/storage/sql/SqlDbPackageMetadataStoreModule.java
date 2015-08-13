package ai.subut.kurjun.metadata.storage.sql;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.model.metadata.PackageMetadataStore;


/**
 * Guice module to initialize package metadata store bindings to SQL db backed metadata store implementations.
 *
 */
public class SqlDbPackageMetadataStoreModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( PackageMetadataStore.class ).to( SqlDbPackageMetadataStore.class );
    }

}

