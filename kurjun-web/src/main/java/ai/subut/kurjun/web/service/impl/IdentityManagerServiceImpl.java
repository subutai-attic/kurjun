package ai.subut.kurjun.web.service.impl;


import java.util.List;

import com.google.inject.Inject;

import ai.subut.kurjun.identity.service.IdentityManager;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.web.service.IdentityManagerService;


/**
 *
 */
public class IdentityManagerServiceImpl implements IdentityManagerService
{
    private IdentityManager identityManager;
    private SecurityManager securityManager;

    @Inject
    public IdentityManagerServiceImpl(IdentityManager identityManager, SecurityManager securityManager)
    {
        this.identityManager = identityManager;
        this.securityManager = securityManager;
    }

    //*************************************
    @Override
    public List<User> getAllUsers()
    {
        return identityManager.getAllUsers();
    }


    //*************************************
    @Override
    public User getUser(String userId)
    {
        return identityManager.getUser( userId );
    }


    //*************************************
    @Override
    public User addUser( String publicKeyASCII )
    {
        return identityManager.addUser( publicKeyASCII );
    }


    //*************************************
    @Override
    public User authenticateUser( String fingerprint, String authzMessage )
    {
        return identityManager.authenticateUser( fingerprint, authzMessage);
    }


    //*************************************
    @Override
    public UserSession loginUser( String fingerprint, String authzMessage )
    {
        return identityManager.loginUser( fingerprint, authzMessage);
    }


    //*************************************
    @Override
    public UserSession loginPublicUser()
    {
        return identityManager.loginPublicUser( );
    }

}
