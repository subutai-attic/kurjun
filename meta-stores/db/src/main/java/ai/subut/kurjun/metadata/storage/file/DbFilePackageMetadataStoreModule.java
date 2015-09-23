package ai.subut.kurjun.metadata.storage.file;


import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import ai.subut.kurjun.metadata.common.MetadataCommonModule;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;


/**
 * Guice module to initialize metadata store bindings to file backed metadata store implementations. A more generic
 * common package meta data module is recommended which installs this module. If you are installing this module
 * explicitly you have to also install {@link MetadataCommonModule}.
 *
 */
public class DbFilePackageMetadataStoreModule extends AbstractModule
{

    /**
     * Property key for file db directory.
     */
    public static final String DB_FILE_LOCATION_NAME = "metadata.storage.file.location";


    @Override
    protected void configure()
    {
        install( new FactoryModuleBuilder()
                .implement( PackageMetadataStore.class, DbFilePackageMetadataStore.class )
                .build( DbFilePackageMetadataStoreFactory.class ) );
    }

}

