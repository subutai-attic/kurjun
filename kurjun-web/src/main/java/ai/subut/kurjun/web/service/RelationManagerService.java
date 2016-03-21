package ai.subut.kurjun.web.service;

import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserSession;

import java.util.List;
import java.util.Set;

/**
 *
 */
public interface RelationManagerService extends BaseService {

    //*************************************
    List<Relation> getAllRelations();

    Relation addTrustRelation(RelationObject source, RelationObject target, RelationObject trustObject,
                              Set<Permission> permissions);

    List<Relation> getTrustRelationsBySource(RelationObject sourceObject );

    List<Relation> getTrustRelationsByTarget(RelationObject targetObject );

    List<Relation> getTrustRelationsByObject(RelationObject trustObject );

    //*************************************
    Relation getRelation( String sourceId, String targetId, String trustObjectId, int trustObjectType );

    //*************************************
    List<Relation> getRelationsByObject( String trustObjectId, int trustObjectType );

    //***************************
    Relation getObjectOwner( String trustObjectId, int trustObjectType );

    //***************************
    Relation buildTrustRelation( User sourceUser, User targetUser, String trustObjectId, int trustObjectType,
                                 Set<Permission> permissions );

    //***************************
    Set<Permission> buildPermissions( int permLevel );

    //***************************
    void checkRelationOwner( UserSession userSession, String objectId, int objectType );

    //***************************
    Set<Permission> checkUserPermissions( UserSession userSession, String objectId, int objectType );

    RelationObject toSourceObject( User user );

    RelationObject toTargetObject( String fingerprint );

    RelationObject toTrustObject( String id, String md5, String name, String version );

}
