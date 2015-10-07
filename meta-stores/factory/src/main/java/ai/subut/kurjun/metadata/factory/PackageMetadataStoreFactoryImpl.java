package ai.subut.kurjun.metadata.factory;


import java.util.Properties;

import com.google.inject.Inject;
import com.google.inject.ProvisionException;

import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.metadata.storage.file.DbFilePackageMetadataStoreFactory;
import ai.subut.kurjun.metadata.storage.nosql.NoSqlPackageMetadataStoreFactory;
import ai.subut.kurjun.metadata.storage.sql.SqlDbPackageMetadataStoreFactory;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;


public class PackageMetadataStoreFactoryImpl implements PackageMetadataStoreFactory
{

    private KurjunProperties properties;
    private DbFilePackageMetadataStoreFactory dbFileMetadataStoreFactory;
    private NoSqlPackageMetadataStoreFactory noSqlMetadataStoreFactory;
    private SqlDbPackageMetadataStoreFactory sqlDbMetadataStoreFactory;


    @Inject
    public PackageMetadataStoreFactoryImpl( KurjunProperties properties,
                                            DbFilePackageMetadataStoreFactory dbFileMetadataStoreFactory,
                                            NoSqlPackageMetadataStoreFactory noSqlMetadataStoreFactory,
                                            SqlDbPackageMetadataStoreFactory sqlDbMetadataStoreFactory )
    {
        this.properties = properties;
        this.dbFileMetadataStoreFactory = dbFileMetadataStoreFactory;
        this.noSqlMetadataStoreFactory = noSqlMetadataStoreFactory;
        this.sqlDbMetadataStoreFactory = sqlDbMetadataStoreFactory;
    }


    @Override
    public PackageMetadataStore create( KurjunContext context )
    {
        Properties cp = properties.getContextProperties( context );
        String type = cp.getProperty( PackageMetadataStoreModule.PACKAGE_METADATA_STORE_TYPE );

        if ( FILE_DB.equals( type ) )
        {
            return dbFileMetadataStoreFactory.create( context );
        }
        if ( NOSQL_DB.equals( type ) )
        {
            return noSqlMetadataStoreFactory.create( context );
        }
        if ( SQL_DB.equals( type ) )
        {
            return sqlDbMetadataStoreFactory.create( context );
        }

        throw new ProvisionException( "Invalid package metadata store type: " + type );
    }


}

