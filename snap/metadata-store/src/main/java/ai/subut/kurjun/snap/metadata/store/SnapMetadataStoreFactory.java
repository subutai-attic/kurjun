package ai.subut.kurjun.snap.metadata.store;


import com.google.inject.name.Named;

import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.model.metadata.snap.SnapMetadataStore;


/**
 * Factory interface to create snap meta data store.
 *
 */
public interface SnapMetadataStoreFactory
{

    /**
     * Creates snap meta data store for the supplied context.
     *
     * @param context
     * @return snap meta data store implementation
     */
    @Named( SnapMetadataStoreModule.FILE_DB )
    SnapMetadataStore create( KurjunContext context );


    /**
     * Creates nosql db backed snap meta data store for the supplied context.
     *
     * @param context context
     * @return snap meta data store impl
     */
    @Named( SnapMetadataStoreModule.NOSQL_DB )
    SnapMetadataStore createCassandraStore( KurjunContext context );
}

