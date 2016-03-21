package ai.subut.kurjun.web.service;


import java.util.List;
import java.util.Set;

import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserSession;


/**
 *
 */
public interface IdentityManagerService extends BaseService
{
    //*************************************
    List<User> getAllUsers();


    //*************************************
    String getPublicUserId();

    //*************************************
    User getUser( String userId );


    //*************************************
    User addUser( String publicKeyASCII );


    //*************************************
    User authenticateUser( String fingerprint, String authzMessage );


    //*************************************
    UserSession loginUser( String fingerprint, String authzMessage );


    //*************************************
    UserSession loginPublicUser();


    //*************************************
    User setSystemOwner( String publicKeyASCII );


    //*************************************
    User getSystemOwner();


    //*************************************
    List<Relation> getAllRelations();

    //*************************************
    Relation getRelation( String relationId );


    //*************************************
    Relation buildTrustRelation( User sourceUser, User targetUser, String trustObjectId, String rclassName,
                                 int trustObjectType, Set<Permission> permissions );

    //*************************************
    Relation buildTrustRelation( RelationObject source, RelationObject target, RelationObject trustObject,
                                 Set<Permission> permissions );
}
