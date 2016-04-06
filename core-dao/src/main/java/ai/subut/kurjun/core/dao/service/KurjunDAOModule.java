package ai.subut.kurjun.core.dao.service;


import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;

import ai.subut.kurjun.core.dao.model.identity.RelationEntity;
import ai.subut.kurjun.core.dao.model.identity.RelationObjectEntity;
import ai.subut.kurjun.core.dao.model.identity.UserEntity;
import ai.subut.kurjun.core.dao.model.identity.UserTokenEntity;
import ai.subut.kurjun.core.dao.model.metadata.RepositoryArtifactEntity;
import ai.subut.kurjun.core.dao.model.metadata.RepositoryDataEntity;
import ai.subut.kurjun.core.dao.service.identity.IdentityDataService;
import ai.subut.kurjun.core.dao.service.identity.IdentityDataServiceImpl;
import ai.subut.kurjun.core.dao.service.identity.RelationDataService;
import ai.subut.kurjun.core.dao.service.identity.RelationDataServiceImpl;
import ai.subut.kurjun.core.dao.service.metadata.RepositoryDataService;
import ai.subut.kurjun.core.dao.service.metadata.RepositoryDataServiceImpl;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserToken;
import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.model.repository.RepositoryArtifact;


/**
 * Guice module to initialize Kurjun DAO bindings.
 */
public class KurjunDAOModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        //****************
        install( new JpaPersistModule( "PU_KURJUN" ) );
        bind( KurjunJPAInitializer.class ).asEagerSingleton();
        //****************

        // -------------------------------------------
        bind( User.class ).to( UserEntity.class );
        bind( UserToken.class ).to( UserTokenEntity.class );
        bind( Relation.class ).to( RelationEntity.class );
        bind( RelationObject.class ).to( RelationObjectEntity.class );

        bind( RepositoryData.class ).to( RepositoryDataEntity.class );
        bind( RepositoryArtifact.class ).to( RepositoryArtifactEntity.class );

        // -------------------------------------------


        // -------------------------------------------
        bind( IdentityDataService.class ).to( IdentityDataServiceImpl.class );
        bind( RelationDataService.class ).to( RelationDataServiceImpl.class );
        bind( RepositoryDataService.class ).to( RepositoryDataServiceImpl.class );
        // -------------------------------------------

    }
}

