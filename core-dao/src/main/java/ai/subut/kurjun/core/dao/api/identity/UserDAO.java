package ai.subut.kurjun.core.dao.api.identity;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.google.inject.Provider;
import ai.subut.kurjun.core.dao.api.GenericDAOImpl;
import ai.subut.kurjun.model.identity.User;


/**
 *
 */
public class UserDAO extends GenericDAOImpl<User>
{

    public UserDAO( final Provider<EntityManager> entityManagerProvider )
    {
        super( entityManagerProvider );
    }
}
