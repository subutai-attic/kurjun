package ai.subut.kurjun.core.dao.api.identity;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger( UserDAO.class );


    public UserDAO()
    {
        super();
    }


    public UserDAO( EntityManagerFactory emf )
    {
        super( emf );
    }


    public User find( String fingerprint ) throws DAOException
    {
        try
        {
            Query q = getEntityManager()
                    .createQuery( " select u from UserEntity u where u.keyFingerprint = :fprint ", UserEntity.class );
            q.setParameter( "fprint", fingerprint );
            List<User> users = q.getResultList();

            return users.size() > 0 ? users.get( 0 ) : null;
        }
        catch ( Exception e )
        {
            LOGGER.error( "\"******************  Error in UserDAO.findByUserFingerprint", e );
            throw new DAOException( e );
        }
    }


    public User findByUsername( String username ) throws DAOException
    {
        try
        {
            Query q = getEntityManager()
                    .createQuery( " select u from UserEntity u where u.userName = :uname ", UserEntity.class );
            q.setParameter( "uname", username );
            List<User> users = q.getResultList();

            return users.size() > 0 ? users.get( 0 ) : null;
        }
        catch ( Exception e )
        {
            LOGGER.error( "\"******************  Error in UserDAO.findByUsername", e );
            throw new DAOException( e );
        }
    }
}
