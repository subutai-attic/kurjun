package ai.subut.kurjun.core.dao.api;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.core.dao.api.identity.IdentityDataService;
import ai.subut.kurjun.core.dao.api.identity.IdentityDataServiceImpl;
import ai.subut.kurjun.core.dao.api.identity.RelationDataService;
import ai.subut.kurjun.core.dao.api.identity.RelationDataServiceImpl;

//import ai.subut.kurjun.core.dao.api.identity.IdentityDataServiceImpl;
//import ai.subut.kurjun.core.dao.api.identity.service.IdentityDataService;


/**
 * Guice module to initialize Kurjun DAO bindings.
 */
public class KurjunDAOModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        //****************
        //install( new JpaPersistModule( "PU-KURJUN" ) );
        //****************

        //bind(KurjunJPAInitializer.class).asEagerSingleton();

        bind( IdentityDataService.class ).to( IdentityDataServiceImpl.class );
        bind( RelationDataService.class ).to( RelationDataServiceImpl.class );
    }
}

