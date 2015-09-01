package ai.subut.kurjun.snap.metadata.store;


import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

import ai.subut.kurjun.model.metadata.snap.SnapMetadataStore;


/**
 * Guice module to initialize snap package store bindings.
 *
 */
public class SnapMetadataStoreModule extends AbstractModule
{
    /**
     * Property key for file db path to be used by file backed meta data store.
     */
    public static final String DB_FILE_PATH = "snap.metadata.store.filedb";

    /**
     * Property key for the snap meta data store type.
     */
    public static final String TYPE = "snap.metadata.store.type";
    public static final String FILE_DB = "filedb";
    public static final String NOSQL_DB = "nosql";


    @Override
    protected void configure()
    {
        install( new FactoryModuleBuilder()
                .implement( SnapMetadataStore.class, Names.named( FILE_DB ), SnapMetadataStoreImpl.class )
                .implement( SnapMetadataStore.class, Names.named( NOSQL_DB ), CassandraSnapMetadataStore.class )
                .build( SnapMetadataStoreFactory.class ) );
    }

}

