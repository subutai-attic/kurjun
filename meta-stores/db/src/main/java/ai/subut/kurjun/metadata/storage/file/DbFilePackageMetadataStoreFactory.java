package ai.subut.kurjun.metadata.storage.file;


import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;


/**
 * Factory interface for file db backed package metadata store implementation.
 *
 */
public interface DbFilePackageMetadataStoreFactory
{
    /**
     * Creates file db backed meta data store for the supplied context.
     *
     * @param context context
     * @return meta data store
     */
    PackageMetadataStore create( KurjunContext context );
}

