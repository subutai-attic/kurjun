package ai.subut.kurjun.metadata.factory;


import java.util.Properties;

import com.google.inject.Inject;
import com.google.inject.ProvisionException;

import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.metadata.storage.file.DbFilePackageMetadataStoreFactory;
import ai.subut.kurjun.metadata.storage.file.DbFilePackageMetadataStoreModule;
import ai.subut.kurjun.metadata.storage.nosql.NoSqlPackageMetadataStoreFactory;
import ai.subut.kurjun.metadata.storage.nosql.NoSqlPackageMetadataStoreModule;
import ai.subut.kurjun.metadata.storage.sql.SqlDbPackageMetadataStoreFactory;
import ai.subut.kurjun.metadata.storage.sql.SqlDbPackageMetadataStoreModule;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;


/**
 * Common package meta data store factory. Basically it is a wrapper of all package meta data store implementation
 * factories.
 *
 */
public class PackageMetadataStoreFactory
{

    @Inject
    private KurjunProperties properties;

    @Inject
    private DbFilePackageMetadataStoreFactory dbFileMetadataStoreFactory;

    @Inject
    private NoSqlPackageMetadataStoreFactory noSqlMetadataStoreFactory;

    @Inject
    private SqlDbPackageMetadataStoreFactory sqlDbMetadataStoreFactory;


    /**
     * Created package meta data store for supplied context. Context properties should provide type of store and other
     * type specific configurations.
     *
     * @param context context for which to create a store
     * @return package meta data store
     */
    public PackageMetadataStore create( KurjunContext context )
    {
        Properties cp = properties.getContextProperties( context );
        String type = cp.getProperty( PackageMetadataStoreModule.PACKAGE_METADATA_STORE_TYPE );

        if ( DbFilePackageMetadataStoreModule.TYPE.equals( type ) )
        {
            return dbFileMetadataStoreFactory.create( context );
        }
        if ( NoSqlPackageMetadataStoreModule.TYPE.equals( type ) )
        {
            return noSqlMetadataStoreFactory.create( context );
        }
        if ( SqlDbPackageMetadataStoreModule.TYPE.equals( type ) )
        {
            return sqlDbMetadataStoreFactory.create( context );
        }

        throw new ProvisionException( "Invalid package metadata store type: " + type );
    }


}

