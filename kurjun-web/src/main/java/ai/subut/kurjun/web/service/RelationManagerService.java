package ai.subut.kurjun.web.service;

import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.UserSession;

import java.util.List;
import java.util.Set;

/**
 *
 */
public interface RelationManagerService extends BaseService {


    //*************************************
    List<Relation> getAllRelations( UserSession userSession );

    //*************************************
    Relation getRelation( UserSession uSession, long relationId );

    //*************************************
    void removeRelation( UserSession uSession, Relation relation );

    //*************************************
    int addTrustRelation( UserSession uSession, String targeObjId, int targetObjType, String trustObjId,
                          int trustObjType, Set<Permission> permissions );

    List<Relation> getRelationsByObject(String id, int objType );

    void changePermissions( UserSession userSession, long relationId, String[] permissions );


}
