package ai.subut.kurjun.metadata.storage.nosql;


import com.datastax.driver.core.Session;
import com.google.inject.AbstractModule;

import ai.subut.kurjun.model.metadata.PackageMetadataStore;


/**
 * Guice module to initialize package metadata store bindings to NoSQL backed metadata store implementations.
 *
 */
public class NoSqlPackageMetadataStoreModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( Session.class ).toProvider( CassandraConnector.getInstance() );

        bind( PackageMetadataStore.class ).to( NoSqlPackageMetadataStore.class );
    }

}

