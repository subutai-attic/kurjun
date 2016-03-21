package ai.subut.kurjun.web.service;

import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.User;

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

    RelationObject toSourceObject( User user );

    RelationObject toTargetObject( String fingerprint );

    RelationObject toTrustObject( String id, String md5, String name, String version );

}
