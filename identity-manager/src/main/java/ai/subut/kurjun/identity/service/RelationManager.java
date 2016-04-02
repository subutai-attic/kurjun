package ai.subut.kurjun.identity.service;


import java.util.List;
import java.util.Set;

import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.User;


/**
 *
 */
public interface RelationManager
{

    //***************************
    Set<Permission> buildPermissions( int permLevel );

    //***************************
    Set<Permission> buildPermissionsAllowAll();

    //***************************
    Set<Permission> buildPermissionsAllowReadWrite();

    //***************************
    Set<Permission> buildPermissionsDenyAll();

    //***************************
    Set<Permission> buildPermissionsDenyDelete();

    //***************************
    RelationObject createRelationObject( String objectId, int objectType );


    //***************************
    Relation buildTrustRelation( User user, String targetObjectId, int targetObjectType,
                                 String trustObjectId, int trustObjectType,
                                 Set<Permission> permissions );

    //***************************
    Relation buildTrustRelation( User sourceUser, User targetUser, String trustObjectId,
                                 int trustObjectType, Set<Permission> permissions );

    //***************************
    Relation buildTrustRelation( User sourceUser, User targetUser, RelationObject trustObject,
                                 Set<Permission> permissions );

    //***************************
    Relation buildTrustRelation( String sourceObjectId, int sourceObjectType, String targetObjectId,
                                 int targetObjectType, String trustObjectId,
                                 int trustObjectType, Set<Permission> permissions );

    //***************************
    Relation buildTrustRelation( RelationObject source, RelationObject target, RelationObject trustObject,
                                 Set<Permission> permissions );


    //***************************
    Relation getRelation( String relationId );


    //********************************************
    List<Relation> getAllRelations();


    //********************************************
    Relation getRelation( String sourceObjectId, String targetObjectId, String trustedObjectId, int objectId );


    //***************************
    List<Relation> getRelationsByObject( RelationObject trustObject );


    //***************************
    List<Relation> getRelationsByObject( String trustObjectId, int trustObjectType );

    //***************************
    Relation getObjectOwner( String trustObjectId, int trustObjectType );


    //***************************
    List<Relation> getRelationsBySource( RelationObject sourceObject );


    //***************************
    List<Relation> getRelationsByTarget( RelationObject targetObject );


    //***************************
    void removeRelation( String relationId );


    //***************************
    Set<Permission> getUserPermissions( User target, String trustObjectId, int trustObjectType );


    //***************************
    void removeRelationsByTrustObject( String trustObjectId, int trustObjectType );
}
