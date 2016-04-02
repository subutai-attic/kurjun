package ai.subut.kurjun.identity;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.core.dao.api.identity.IdentityDataService;
import ai.subut.kurjun.core.dao.api.identity.IdentityDataServiceImpl;
import ai.subut.kurjun.core.dao.api.identity.RelationDataServiceImpl;
import ai.subut.kurjun.identity.service.IdentityManager;
import ai.subut.kurjun.core.dao.api.identity.RelationDataService;
import ai.subut.kurjun.identity.service.RelationManager;
import ai.subut.kurjun.security.manager.SecurityManagerImpl;
import ai.subut.kurjun.security.manager.service.SecurityManager;


/**
 *
 */

public class KurjunIdentityModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( SecurityManager.class ).to( SecurityManagerImpl.class );

        bind( IdentityManager.class ).to( IdentityManagerImpl.class );

        bind( RelationManager.class ).to( RelationManagerImpl.class );
    }

}

