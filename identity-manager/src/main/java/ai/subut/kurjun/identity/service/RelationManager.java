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
    RelationObject createRelationObject( String objectId, String className, int objectType );


    //***************************
    Relation buildTrustRelation( User user, String targetObjectId, String tclassName, int targetObjectType,
                                 String trustObjectId, String rclassName, int trustObjectType,
                                 Set<Permission> permissions );

    //***************************
    Relation buildTrustRelation( User sourceUser, User targetUser, String trustObjectId, String rclassName,
                                 int trustObjectType, Set<Permission> permissions );

    //***************************
    Relation buildTrustRelation( User sourceUser, User targetUser, RelationObject trustObject,
                                 Set<Permission> permissions );

    //***************************
    Relation buildTrustRelation( String sourceObjectId, String sclassName, int sourceObjectType, String targetObjectId,
                                 String tclassName, int targetObjectType, String trustObjectId, String rclassName,
                                 int trustObjectType, Set<Permission> permissions );

    //***************************
    Relation buildTrustRelation( RelationObject source, RelationObject target, RelationObject trustObject,
                                 Set<Permission> permissions );


    //***************************
    Relation saveTrustRelation( Relation relation );


    //***************************
    Relation getRelation( String relationId );


    //********************************************
    List<Relation> getAllRelations();

    //***************************
    List<Relation> getRelationsByObject( RelationObject trustObject );


    //***************************
    List<Relation> getRelationsBySource( RelationObject sourceObject );


    //***************************
    List<Relation> getRelationsByTarget( RelationObject targetObject );


    //***************************
    void removeRelation( String relationId );
}
