package ai.subut.kurjun.core.dao.api;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.core.dao.api.identity.IdentityDataService;
import ai.subut.kurjun.core.dao.api.identity.IdentityDataServiceImpl;
import ai.subut.kurjun.core.dao.api.identity.RelationDataService;
import ai.subut.kurjun.core.dao.api.identity.RelationDataServiceImpl;
import ai.subut.kurjun.core.dao.model.identity.UserEntity;
import ai.subut.kurjun.core.dao.model.identity.UserTokenEntity;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserToken;


/**
 * Guice module to initialize Kurjun DAO bindings.
 */
public class KurjunDAOModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        //****************
        //install( new JpaPersistModule( "PU_KURJUN" ) );
        //bind(KurjunJPAInitializer.class).asEagerSingleton();
        //****************

        bind( User.class ).to( UserEntity.class );
        bind( UserToken.class ).to( UserTokenEntity.class );

        bind( IdentityDataService.class ).to( IdentityDataServiceImpl.class );
        bind( RelationDataService.class ).to( RelationDataServiceImpl.class );
    }
}

