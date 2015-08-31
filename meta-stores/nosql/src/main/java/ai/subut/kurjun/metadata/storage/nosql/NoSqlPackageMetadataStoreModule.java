package ai.subut.kurjun.metadata.storage.nosql;


import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import ai.subut.kurjun.metadata.common.MetadataCommonModule;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;


/**
 * Guice module to initialize package metadata store bindings to NoSQL backed metadata store implementations. A more
 * generic common package meta data module is recommended which installs this module. If you are installing this module
 * explicitly you have to also install {@link MetadataCommonModule}.
 *
 */
public class NoSqlPackageMetadataStoreModule extends AbstractModule
{

    public static final String TYPE = "nosql";


    @Override
    protected void configure()
    {
        Module module = new FactoryModuleBuilder()
                .implement( PackageMetadataStore.class, NoSqlPackageMetadataStore.class )
                .build( NoSqlPackageMetadataStoreFactory.class );

        install( module );
    }

}

