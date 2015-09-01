package ai.subut.kurjun.metadata.storage.nosql;


import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;


/**
 * Factory interface to create nosql db backed implementation of package meta data store.
 *
 */
public interface NoSqlPackageMetadataStoreFactory
{

    /**
     * Creates nosql db backed package meta data store for the supplied context.
     *
     * @param context context by which a uniquely identified schema is selected; can be {@code null} in which case a
     * common schema is used for all context independent instances
     * @return package meta data store
     */
    PackageMetadataStore create( KurjunContext context );

}

