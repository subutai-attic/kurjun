package ai.subut.kurjun.metadata.factory;


import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;


/**
 * Common package meta data store factory. Basically it is a wrapper of all package meta data store implementation
 * factories.
 *
 */
public interface PackageMetadataStoreFactory
{

    String FILE_DB = "file";
    String SQL_DB = "sql";
    String NOSQL_DB = "nosql";


    /**
     * Created package meta data store for supplied context. Context properties should provide type of store and other
     * type specific configurations.
     *
     * @param context context for which to create a store
     * @return package meta data store
     */
    PackageMetadataStore create( KurjunContext context );

}

