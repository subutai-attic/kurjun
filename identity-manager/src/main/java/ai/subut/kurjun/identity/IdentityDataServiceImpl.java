package ai.subut.kurjun.identity;


import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.identity.UserDAO;
import ai.subut.kurjun.identity.service.IdentityDataService;
import ai.subut.kurjun.model.identity.User;


/**
 *
 */
public class IdentityDataServiceImpl implements IdentityDataService
{
    @Inject
    UserDAO userDAO;


    public IdentityDataServiceImpl()
    {
    }

    //*****************************
    @Override
    public void persistUser( User user )
    {
        try
        {
            userDAO.persist( user );
        }
        catch ( DAOException e )
        {

        }
    }


    //*****************************
    @Override
    public User getUser(String fingerprint)
    {
        try
        {
            return userDAO.find( fingerprint );
        }
        catch ( DAOException e )
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
