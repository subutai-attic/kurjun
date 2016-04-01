package ai.subut.kurjun.identity;


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


}
