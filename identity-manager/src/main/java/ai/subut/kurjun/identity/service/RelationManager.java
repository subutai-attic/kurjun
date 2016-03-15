package ai.subut.kurjun.identity.service;


import java.util.List;

import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.RelationObject;


/**
 *
 */
public interface RelationManager
{
    //***************************
    Relation buildTrustRelation( RelationObject source, RelationObject target, RelationObject trustObject );

    //***************************
    Relation getRelation( String relationId );

    //***************************
    List<Relation> getRelationsByObject( RelationObject trustObject );

    //***************************
    List<Relation> getRelationsBySource( RelationObject sourceObject );

    //***************************
    List<Relation> getRelationsByTarget( RelationObject targetObject );

    //***************************
    void removeRelation( String relationId );
}
