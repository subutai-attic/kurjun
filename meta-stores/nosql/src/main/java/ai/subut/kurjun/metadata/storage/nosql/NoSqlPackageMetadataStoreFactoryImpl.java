package ai.subut.kurjun.metadata.storage.nosql;


import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;


public class NoSqlPackageMetadataStoreFactoryImpl implements NoSqlPackageMetadataStoreFactory
{

    private CassandraSessionProvider sessionProvider;


    public NoSqlPackageMetadataStoreFactoryImpl( CassandraSessionProvider sessionProvider )
    {
        this.sessionProvider = sessionProvider;
    }


    @Override
    public PackageMetadataStore create( KurjunContext context )
    {
        return new NoSqlPackageMetadataStore( sessionProvider, context );
    }

}

