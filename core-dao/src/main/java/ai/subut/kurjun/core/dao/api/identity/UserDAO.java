package ai.subut.kurjun.core.dao.api.identity;


import java.util.List;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.GenericDAOImpl;
import ai.subut.kurjun.core.dao.model.identity.UserEntity;
import ai.subut.kurjun.model.identity.User;


/**
 *
 */
public class UserDAO extends GenericDAOImpl<User>
{

    @Inject
    public UserDAO( final Provider<EntityManager> entityManagerProvider )
    {
        super( entityManagerProvider );
    }

    @Transactional
    public User find(String fingerprint) throws DAOException
    {
        try
        {
            return entityManagerProvider.get().find( UserEntity.class, fingerprint );
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }
    }

}
