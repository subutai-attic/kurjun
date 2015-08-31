package ai.subut.kurjun.snap.metadata.store;


import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import ai.subut.kurjun.model.metadata.snap.SnapMetadataStore;


/**
 * Guice module to initialize snap package store bindings.
 *
 */
public class SnapMetadataStoreModule extends AbstractModule
{
    public static final String DB_FILE_PATH = "snap.metadata.store.filedb";


    @Override
    protected void configure()
    {
        install( new FactoryModuleBuilder()
                .implement( SnapMetadataStore.class, SnapMetadataStoreImpl.class )
                .build( SnapMetadataStoreFactory.class ) );
    }

}

