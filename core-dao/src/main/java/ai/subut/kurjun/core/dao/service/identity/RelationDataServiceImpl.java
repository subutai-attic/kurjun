package ai.subut.kurjun.core.dao.service.identity;


import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.core.dao.api.identity.RelationDAO;
import ai.subut.kurjun.core.dao.api.identity.RelationObjectDAO;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.RelationObject;


/**
 *
 */
@Singleton
public class RelationDataServiceImpl implements RelationDataService
{

    private RelationDAO relationDAO;
    private RelationObjectDAO relationObjectDAO;

    @Inject
    public RelationDataServiceImpl(RelationDAO relationDAO, RelationObjectDAO relationObjectDAO)
    {
        this.relationDAO = relationDAO;
        this.relationObjectDAO = relationObjectDAO;
    }


    //***************************
    @Override
    public void persistRelation( Relation relation )
    {
        try
        {
            if(relation != null)
                relationDAO.persist( relation );
        }
        catch ( Exception ex )
        {
        }
    }


    //***************************
    @Override
    public Relation mergeRelation( Relation relation )
    {
        try
        {
            if(relation != null)
                return relationDAO.merge( relation );
        }
        catch ( Exception ex )
        {
            return null;
        }

        return null;
    }


    //***************************
    @Override
    public Relation getRelation( long relationId)
    {
        try
        {
            return relationDAO.find( relationId );
        }
        catch ( Exception ex )
        {
            return null;
        }

    }


    //***************************
    @Override
    public List<Relation> getAllRelations()
    {
        try
        {
            return relationDAO.findAll( "RelationEntity" );
        }
        catch ( Exception ex )
        {
            return null;
        }

    }


    //***************************
    @Override
    public List<Relation> getRelationsBySource( String sourceObjId, int sourceObjType )
    {
        try
        {
            return relationDAO.findBySourceObject( sourceObjId, sourceObjType);
        }
        catch ( Exception ex )
        {
            return null;
        }

    }


    //***************************
    @Override
    public List<Relation> getRelationsBySource( RelationObject relationObject )
    {
        try
        {
            return relationDAO.findBySourceObject( relationObject.getUniqID(), relationObject.getType());
        }
        catch ( Exception ex )
        {
            return null;
        }

    }

    //***************************
    @Override
    public List<Relation> getRelationsByTarget( String targetObjId, int targetObjType )
    {
        try
        {
            return relationDAO.findBySourceObject( targetObjId, targetObjType);
        }
        catch ( Exception ex )
        {
            return null;
        }

    }


    //***************************
    @Override
    public List<Relation> getRelationsByTarget( RelationObject relationObject )
    {
        try
        {
            return relationDAO.findByTargetObject( relationObject.getUniqID(), relationObject.getType() );
        }
        catch ( Exception ex )
        {
            return null;
        }

    }


    //***************************
    @Override
    public List<Relation> getRelationsByTrustObject( String trustObjId, int trustObjType )
    {
        try
        {
            return relationDAO.findByTrustObject( trustObjId, trustObjType );
        }
        catch ( Exception ex )
        {
            return null;
        }

    }


    //***************************
    @Override
    public List<Relation> getRelationsByTrustObject( RelationObject relationObject )
    {
        try
        {
            return relationDAO.findByTrustObject( relationObject.getUniqID(), relationObject.getType() );
        }
        catch ( Exception ex )
        {
            return null;
        }

    }


    //***************************
    @Override
    public Relation getTrustObjectOwner( String objectId, int objectType )
    {
        try
        {
            return relationDAO.findTrustObjectOwner( objectId, objectType );
        }
        catch ( Exception ex )
        {
            return null;
        }

    }


    //***************************
    @Override
    public void removeByTrustObject( String objectId, int objectType )
    {
        try
        {
            relationDAO.removeByTrustObject( objectId, objectType );
        }
        catch ( Exception ex )
        {
        }
    }



    //***************************
    @Override
    public void removeRelation( Relation entity )
    {
        try
        {
            relationDAO.remove( entity );
        }
        catch ( Exception ex )
        {
        }

    }


    //***************************
    @Override
    public void removeRelation( long id)
    {
        try
        {
            Relation relation = relationDAO.find( id );

            if(relation != null)
            {
                relationDAO.remove(relation  );
            }
        }
        catch ( Exception ex )
        {
        }

    }


    //***************************
    @Override
    public RelationObject getRelationObject( RelationObject relationObject )
    {
        try
        {
            return relationObjectDAO.find( relationObject.getUniqID(), relationObject.getType());

        }
        catch ( Exception ex )
        {
            return null;
        }
    }


    //***************************
    @Override
    public void removeRelationObject( String id, int type )
    {
        try
        {
            RelationObject relationObject = relationObjectDAO.find( id, type );
            removeRelationObject( relationObject );
        }
        catch ( Exception ex )
        {
        }
    }


    //***************************
    @Override
    public void removeRelationObject( RelationObject relationObject )
    {
        try
        {
            if(relationObject != null)
            {
                relationObjectDAO.remove( relationObject  );
            }
        }
        catch ( Exception ex )
        {
        }
    }


}
