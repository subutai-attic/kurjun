package ai.subut.kurjun.core.dao.service.identity;


import java.util.List;

import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.RelationObject;


/**
 *
 */
public interface RelationDataService
{

    //***************************
    void persistRelation( Relation relation );


    //***************************
    Relation mergeRelation( Relation relation );


    //***************************
    Relation getRelation( long relationId );


    //***************************
    List<Relation> getAllRelations();


    //***************************
    List<Relation> getRelationsBySource( String sourceObjId, int sourceObjType );


    //***************************
    List<Relation> getRelationsBySource( RelationObject relationObject );

    //***************************
    List<Relation> getRelationsByTarget( String targetObjId, int targetObjType );

    //***************************
    List<Relation> getRelationsByTarget( RelationObject relationObject );

    //***************************
    List<Relation> getRelationsByTrustObject( String trustObjId, int trustObjType );

    //***************************
    List<Relation> getRelationsByTrustObject( RelationObject relationObject );

    //***************************
    Relation getTrustObjectOwner( String objectId, int objectType );

    //***************************
    void removeByTrustObject( String objectId, int objectType );

    //***************************
    void removeRelation( Relation entity );


    //***************************
    void removeRelation( long id );


    //***************************
    RelationObject getRelationObject( RelationObject relationObject );


    //***************************
    void removeRelationObject( String id, int type );


    //***************************
    void removeRelationObject( RelationObject relationObject );
}
