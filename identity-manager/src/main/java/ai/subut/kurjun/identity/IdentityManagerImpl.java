package ai.subut.kurjun.identity;


import ai.subut.kurjun.identity.service.IdentityManager;
import com.google.inject.Inject;

import ai.subut.kurjun.identity.service.RelationManager;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.security.manager.service.SecurityManager;


/**
 *
 */
public class IdentityManagerImpl implements IdentityManager
{

    //***************************
    @Inject
    private SecurityManager securityManager;

    @Inject
    private RelationManager relationManager;


    //***************************


    public IdentityManagerImpl()
    {

    }


    //********************************************
    @Override
    public User authenticateUser( String userName, String password )
    {
        return null;
    }


    //********************************************
    @Override
    public User authenticateByToken( String token)
    {
        return null;
    }

}
