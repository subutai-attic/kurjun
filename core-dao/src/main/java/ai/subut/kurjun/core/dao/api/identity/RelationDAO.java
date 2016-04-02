package ai.subut.kurjun.core.dao.api.identity;


import javax.persistence.EntityManager;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.GenericDAOImpl;
import ai.subut.kurjun.core.dao.model.identity.UserEntity;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.User;


/**
 *
 */
public class RelationDAO  extends GenericDAOImpl<Relation>
{

    /*
    @Inject
    public RelationDAO( final Provider<EntityManager> entityManagerProvider )
    {
        super( entityManagerProvider );
    }*/

    public RelationDAO()
    {
        super();
    }

    @Transactional
    public User find(long id) throws DAOException
    {
        try
        {
            return getEntityManager().find( UserEntity.class, id );
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }
    }
}
