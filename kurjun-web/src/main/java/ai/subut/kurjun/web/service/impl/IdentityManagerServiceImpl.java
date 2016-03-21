package ai.subut.kurjun.web.service.impl;


import java.util.List;
import java.util.Set;

import com.google.inject.Inject;

import ai.subut.kurjun.identity.service.IdentityManager;
import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.RelationObject;
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

    private UserSession userSession;


    @Inject
    public IdentityManagerServiceImpl( IdentityManager identityManager, SecurityManager securityManager )
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
    public String getPublicUserId()
    {
        return identityManager.getPublicUserId();
    }


    //*************************************
    @Override
    public User getUser( String userId )
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
        return identityManager.authenticateUser( fingerprint, authzMessage );
    }


    //*************************************
    @Override
    public UserSession loginUser( String fingerprint, String authzMessage )
    {
        return identityManager.loginUser( fingerprint, authzMessage );
    }


    //*************************************
    @Override
    public UserSession loginPublicUser()
    {
        return identityManager.loginPublicUser();
    }


    //*************************************
    @Override
    public User setSystemOwner( String publicKeyASCII )
    {
        return identityManager.setSystemOwner( publicKeyASCII );
    }


    //*************************************
    @Override
    public User getSystemOwner()
    {
        return identityManager.getSystemOwner();
    }


    //*************************************
    @Override
    public void setUserSession( UserSession userSession )
    {
        this.userSession = userSession;
    }


    //*************************************
    @Override
    public UserSession getUserSession()
    {
        return this.userSession;
    }


    //*************************************
    @Override
    public List<Relation> getAllRelations()
    {
        return identityManager.getRelationManager().getAllRelations();
    }


    //*************************************
    @Override
    public Relation getRelation( String relationId )
    {
        return identityManager.getRelationManager().getRelation( relationId );
    }


    //*************************************
    @Override
    public Relation buildTrustRelation( User sourceUser, User targetUser, String trustObjectId, String rclassName,
                                        int trustObjectType, Set<Permission> permissions )
    {
        return identityManager.getRelationManager()
                              .buildTrustRelation( sourceUser, targetUser, trustObjectId, rclassName, trustObjectType,
                                      permissions );
    }


    //*************************************
    @Override
    public Relation buildTrustRelation( RelationObject source, RelationObject target, RelationObject trustObject,
                                        Set<Permission> permissions )
    {
        return identityManager.getRelationManager().buildTrustRelation( source,target,trustObject,permissions );
    }


}
