package ai.subut.kurjun.metadata.storage.file;


import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;


class DbFilePackageMetadataStoreFactoryImpl implements DbFilePackageMetadataStoreFactory
{

    private KurjunProperties kurjunProperties;


    public DbFilePackageMetadataStoreFactoryImpl( KurjunProperties kurjunProperties )
    {
        this.kurjunProperties = kurjunProperties;
    }


    @Override
    public PackageMetadataStore create( KurjunContext context )
    {
        return new DbFilePackageMetadataStore( kurjunProperties, context );
    }

}

