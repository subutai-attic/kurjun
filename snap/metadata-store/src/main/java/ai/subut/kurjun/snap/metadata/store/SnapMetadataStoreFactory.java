package ai.subut.kurjun.snap.metadata.store;


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
    SnapMetadataStore create( KurjunContext context );
}

