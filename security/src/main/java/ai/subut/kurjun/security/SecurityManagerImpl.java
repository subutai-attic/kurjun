package ai.subut.kurjun.security;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.subut.kurjun.model.security.Identity;
import ai.subut.kurjun.security.service.IdentityManager;
import ai.subut.kurjun.security.service.SecurityManager;


class SecurityManagerImpl implements SecurityManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger( SecurityManagerImpl.class );

    private IdentityManager identityManager;


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
    public boolean isAllowed( String fingerprint, String itemId )
    {
        throw new UnsupportedOperationException( "TODO: " );
    }

}

