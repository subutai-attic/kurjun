package ai.subut.kurjun.metadata.factory;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.metadata.common.MetadataCommonModule;
import ai.subut.kurjun.metadata.storage.file.DbFilePackageMetadataStoreModule;
import ai.subut.kurjun.metadata.storage.nosql.NoSqlPackageMetadataStoreModule;
import ai.subut.kurjun.metadata.storage.sql.SqlDbPackageMetadataStoreModule;


/**
 * Guice module to initialize package meta data store bindings and factories.
 *
 */
public class PackageMetadataStoreModule extends AbstractModule
{

    public static final String PACKAGE_METADATA_STORE_TYPE = "package.metadata.factory.type";


    @Override
    protected void configure()
    {
        // install all package metadata store implementation modules here
        install( new MetadataCommonModule() );
        install( new DbFilePackageMetadataStoreModule() );
        install( new NoSqlPackageMetadataStoreModule() );
        install( new SqlDbPackageMetadataStoreModule() );

        bind( PackageMetadataStoreFactory.class ).to( PackageMetadataStoreFactoryImpl.class );
    }

}

