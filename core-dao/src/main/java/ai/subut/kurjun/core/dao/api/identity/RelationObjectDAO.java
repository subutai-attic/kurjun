package ai.subut.kurjun.core.dao.api.identity;


import java.util.List;

import javax.persistence.Query;

import com.google.inject.persist.Transactional;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.GenericDAOImpl;
import ai.subut.kurjun.core.dao.model.identity.RelationObjectEntity;
import ai.subut.kurjun.model.identity.RelationObject;


/**
 *
 */
public class RelationObjectDAO extends GenericDAOImpl<RelationObject>
{

    public RelationObjectDAO()
    {
        super();
    }


    //***********************************************
    @Transactional
    public RelationObject find( String id ) throws DAOException
    {
        try
        {
            //RelationObjectEntityPk Pk = new RelationObjectEntityPk(objectId, objectType);

            return getEntityManager().find( RelationObjectEntity.class, id );
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }
    }


    //***********************************************
    @Transactional
    public RelationObject findByUniqID( String objectId, int objectType ) throws DAOException
    {
        try
        {
            Query qr = getEntityManager().createQuery(
                    " select e from RelationObjectEntity e where e.objectId=:objectId and e.type=:objectType",
                    RelationObjectEntity.class );

            qr.setParameter( "objectId", objectId );
            qr.setParameter( "objectType", objectType );

            //**************************************
            List<RelationObject> relations = qr.getResultList();

            if ( relations.isEmpty() )
            {
                return null;
            }
            else
            {
                return relations.get( 0 );
            }
            //**************************************

        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }
    }
}
