package ai.subut.kurjun.metadata.storage.sql;


import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;


/**
 * Factory interface for SQL db backed package meta data store.
 *
 */
public interface SqlDbPackageMetadataStoreFactory
{

    /**
     * Creates SQL db backed package meta data store for the supplied context.
     *
     * @param context
     * @return
     */
    PackageMetadataStore create( KurjunContext context );

}

