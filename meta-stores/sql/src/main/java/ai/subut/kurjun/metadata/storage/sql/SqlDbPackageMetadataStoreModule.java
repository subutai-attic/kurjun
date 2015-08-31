package ai.subut.kurjun.metadata.storage.sql;


import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import ai.subut.kurjun.metadata.common.MetadataCommonModule;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;


/**
 * Guice module to initialize package metadata store bindings to SQL db backed metadata store implementations. A more
 * generic common package meta data module is recommended which installs this module. If you are installing this module
 * explicitly you have to also install {@link MetadataCommonModule}.
 *
 */
public class SqlDbPackageMetadataStoreModule extends AbstractModule
{

    public static final String TYPE = "sql";


    @Override
    protected void configure()
    {
        install( new FactoryModuleBuilder()
                .implement( PackageMetadataStore.class, SqlDbPackageMetadataStore.class )
                .build( SqlDbPackageMetadataStoreFactory.class ) );
    }

}

