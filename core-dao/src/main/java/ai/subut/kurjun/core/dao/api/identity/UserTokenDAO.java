package ai.subut.kurjun.core.dao.api.identity;


import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.GenericDAOImpl;
import ai.subut.kurjun.core.dao.model.identity.UserTokenEntity;
import ai.subut.kurjun.model.identity.UserToken;


/**
 *
 */
public class UserTokenDAO  extends GenericDAOImpl<UserToken>
{
    private static final Logger LOGGER = LoggerFactory.getLogger( UserTokenDAO.class );

    public UserTokenDAO()
    {
        super();
    }

    public UserTokenDAO(EntityManagerFactory emf)
    {
        super(emf);
    }


    @Transactional
    public UserToken find(String token) throws DAOException
    {
        try
        {
            return getEntityManager().find( UserTokenEntity.class, token );
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }
    }
}
