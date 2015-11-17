package ai.subut.kurjun.security;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.security.service.AuthManager;
import ai.subut.kurjun.security.service.FileDbProvider;
import ai.subut.kurjun.security.service.GroupManager;
import ai.subut.kurjun.security.service.IdentityManager;
import ai.subut.kurjun.security.service.PgpKeyFetcher;
import ai.subut.kurjun.security.service.RoleManager;


/**
 * Guice module to initialize security related class bindings.
 *
 */
public class SecurityModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( FileDbProvider.class ).to( FileDbProviderImpl.class );
        bind( PgpKeyFetcher.class ).to( PgpKeyFetcherImpl.class );

        bind( GroupManager.class ).to( GroupManagerImpl.class );
        bind( IdentityManager.class ).to( IdentityManagerImpl.class );
        bind( RoleManager.class ).to( RoleManagerImpl.class );
        bind( AuthManager.class ).to( AuthManagerImpl.class );
    }

}

