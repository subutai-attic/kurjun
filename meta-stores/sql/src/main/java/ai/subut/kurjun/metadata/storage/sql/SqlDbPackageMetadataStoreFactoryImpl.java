package ai.subut.kurjun.metadata.storage.sql;


import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;


public class SqlDbPackageMetadataStoreFactoryImpl implements SqlDbPackageMetadataStoreFactory
{
    private KurjunProperties kurjunProperties;


    public SqlDbPackageMetadataStoreFactoryImpl( KurjunProperties kurjunProperties )
    {
        this.kurjunProperties = kurjunProperties;
    }


    @Override
    public PackageMetadataStore create( KurjunContext context )
    {
        return new SqlDbPackageMetadataStore( kurjunProperties, context );
    }

}

