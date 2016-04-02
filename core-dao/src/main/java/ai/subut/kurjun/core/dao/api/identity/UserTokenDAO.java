package ai.subut.kurjun.core.dao.api.identity;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger( UserTokenDAO.class );

    public UserTokenDAO()
    {
        super();
    }

    public User find(String token) throws DAOException
    {
        try
        {
            return getEntityManager().find( UserEntity.class, token );
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }
    }
}
