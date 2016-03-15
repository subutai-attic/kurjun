package ai.subut.kurjun.identity;


import java.util.List;

import ai.subut.kurjun.identity.service.RelationManager;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.RelationObject;


/**
 *
 */
public class RelationManagerImpl implements RelationManager
{

    //***************************
    public RelationManagerImpl()
    {

    }


    //***************************
    @Override
    public Relation buildTrustRelation( RelationObject source, RelationObject target, RelationObject trustObject )
    {
        return null;
    }


    //***************************
    @Override
    public Relation getRelation( String relationId )
    {
        return null;
    }


    //***************************
    @Override
    public List<Relation> getRelationsByObject( final RelationObject trustObject )
    {
        return null;
    }


    //***************************
    @Override
    public List<Relation> getRelationsBySource( final RelationObject sourceObject )
    {
        return null;
    }


    //***************************
    @Override
    public List<Relation> getRelationsByTarget( final RelationObject targetObject )
    {
        return null;
    }


    //***************************
    @Override
    public void removeRelation( final String relationId )
    {

    }

}
