package ai.subut.kurjun.core.dao.api.identity;


import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.GenericDAOImpl;
import ai.subut.kurjun.core.dao.model.identity.RelationObjectEntity;
import ai.subut.kurjun.core.dao.model.identity.RelationObjectEntityPk;
import ai.subut.kurjun.model.identity.RelationObject;


/**
 *
 */
public class RelationObjectDAO   extends GenericDAOImpl<RelationObject>
{

    public RelationObjectDAO()
    {
        super();
    }


    public RelationObject find(String objectId, int objectType) throws DAOException
    {
        try
        {
            RelationObjectEntityPk Pk = new RelationObjectEntityPk(objectId, objectType);

            return getEntityManager().find( RelationObjectEntity.class, Pk );
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }
    }
}
