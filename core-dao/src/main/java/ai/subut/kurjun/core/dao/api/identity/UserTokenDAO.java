package ai.subut.kurjun.core.dao.api.identity;


import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.GenericDAOImpl;
import ai.subut.kurjun.core.dao.model.identity.UserEntity;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserToken;


/**
 *
 */
public class UserTokenDAO  extends GenericDAOImpl<UserToken>
{
    //@Inject
    //public UserTokenDAO( final Provider<EntityManager> entityManagerProvider )
    //{
      //  super( entityManagerProvider );
    //}

    public UserTokenDAO()
    {
        super();
    }

    @Transactional
    public User find(String token) throws DAOException
    {
        try
        {
            //return entityManagerProvider.get().find( UserEntity.class, token );
            return getEntityManager().find( UserEntity.class, token );
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }
    }
}
