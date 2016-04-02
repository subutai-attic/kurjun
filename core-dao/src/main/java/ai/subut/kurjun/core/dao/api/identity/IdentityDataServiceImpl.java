package ai.subut.kurjun.core.dao.api.identity;


import java.util.Collections;
import java.util.List;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.model.identity.User;


/**
 *
 */
@Singleton
public class IdentityDataServiceImpl implements IdentityDataService
{
    private UserDAO userDAO;

    @Inject
    public IdentityDataServiceImpl(UserDAO userDAO)
    {
        this.userDAO = userDAO;
    }

    //*****************************
    @Override
    public void persistUser( User user )
    {
        try
        {
            if(user != null)
                userDAO.persist( user );
        }
        catch ( DAOException e )
        {

        }
    }


    //*****************************
    @Override
    public User getUser( String fingerprint )
    {
        try
        {
            if( !Strings.isNullOrEmpty(fingerprint))
                return userDAO.find( fingerprint );
            else
                return null;
        }
        catch ( Exception e )
        {
            return null;
        }
    }


    //*****************************
    @Override
    public List<User> getAllUsers()
    {
        try
        {
            return userDAO.findAll( User.class );
        }
        catch ( DAOException e )
        {
            return Collections.emptyList();
        }
    }


}
