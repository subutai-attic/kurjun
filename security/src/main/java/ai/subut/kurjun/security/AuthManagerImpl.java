package ai.subut.kurjun.security;


import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.model.security.Identity;
import ai.subut.kurjun.model.security.Permission;
import ai.subut.kurjun.model.security.Role;
import ai.subut.kurjun.security.service.AuthManager;
import ai.subut.kurjun.security.service.IdentityManager;


class AuthManagerImpl implements AuthManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AuthManagerImpl.class );

    private IdentityManager identityManager;


    @Inject
    public AuthManagerImpl( IdentityManager identityManager )
    {
        this.identityManager = identityManager;
    }


    @Override
    public boolean isAuthenticated( String fingerprint )
    {
        try
        {
            Identity id = identityManager.getIdentity( fingerprint );
            return id != null;
        }
        catch ( IOException ex )
        {
            LOGGER.info( "Failed to get identity", ex );
            return false;
        }
    }


    @Override
    public boolean isAllowed( String fingerprint, Permission permission, KurjunContext context )
    {
        try
        {
            Identity id = identityManager.getIdentity( fingerprint );
            Set<Role> roles = identityManager.getRoles( id, context );
            for ( Role role : roles )
            {
                if ( role.hasPermission( permission ) )
                {
                    return true;
                }
            }
        }
        catch ( IOException ex )
        {
            LOGGER.info( "Failed to get identity data", ex );
        }
        return false;
    }


}

