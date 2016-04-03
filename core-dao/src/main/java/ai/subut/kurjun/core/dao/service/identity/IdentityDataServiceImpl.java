package ai.subut.kurjun.core.dao.service.identity;


import java.util.Collections;
import java.util.List;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.identity.UserDAO;
import ai.subut.kurjun.core.dao.api.identity.UserTokenDAO;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserToken;
import ninja.jpa.UnitOfWork;


/**
 *
 */
@Singleton
public class IdentityDataServiceImpl implements IdentityDataService
{
    private UserDAO userDAO;
    private UserTokenDAO userTokenDAO;

    @Inject
    public IdentityDataServiceImpl(UserDAO userDAO, UserTokenDAO userTokenDAO)
    {
        this.userDAO = userDAO;
        this.userTokenDAO = userTokenDAO;
    }


    //*****************************
    @UnitOfWork
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
    @UnitOfWork
    @Override
    public User mergeUser( User user )
    {
        try
        {
            if(user != null)
                return userDAO.merge( user );
        }
        catch ( DAOException e )
        {
            return null;
        }

        return null;
    }


    //*****************************
    @UnitOfWork
    @Override
    public User getUser( String fingerprint )
    {
        try
        {
            if( !Strings.isNullOrEmpty(fingerprint))
                return userDAO.find( fingerprint.toLowerCase() );
            else
                return null;
        }
        catch ( Exception e )
        {
            return null;
        }
    }


    //*****************************
    @UnitOfWork
    @Override
    public List<User> getAllUsers()
    {
        try
        {
            return userDAO.findAll( "UserEntity");
        }
        catch ( DAOException e )
        {
            return Collections.emptyList();
        }
    }



    //*****************************
    @UnitOfWork
    @Override
    public List<UserToken> getAllTokens()
    {
        try
        {
            return userTokenDAO.findAll( "UserTokenEntity");
        }
        catch ( DAOException e )
        {
            return Collections.emptyList();
        }
    }


    //*****************************
    @UnitOfWork
    @Override
    public UserToken  mergeUserToken( UserToken userToken)
    {
        try
        {
            if(userToken != null)
                return userTokenDAO.merge( userToken );
        }
        catch ( DAOException e )
        {
            return null;
        }

        return null;
    }



    //*****************************
    @UnitOfWork
    @Override
    public UserToken getUserToken( String fingerprint )
    {
        try
        {
            if( !Strings.isNullOrEmpty(fingerprint))
                return userTokenDAO.find( fingerprint.toLowerCase() );
            else
                return null;
        }
        catch ( Exception e )
        {
            return null;
        }
    }


}
