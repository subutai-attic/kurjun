package ai.subut.kurjun.core.dao.service.identity;


import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.identity.UserDAO;
import ai.subut.kurjun.core.dao.api.identity.UserTokenDAO;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserToken;



/**
 *
 */
@Singleton
public class IdentityDataServiceImpl implements IdentityDataService
{
    private static Logger LOGGER = LoggerFactory.getLogger( IdentityDataServiceImpl.class );

    private UserDAO userDAO;
    private UserTokenDAO userTokenDAO;

    @Inject
    public IdentityDataServiceImpl(UserDAO userDAO, UserTokenDAO userTokenDAO)
    {
        this.userDAO = userDAO;
        this.userTokenDAO = userTokenDAO;
    }

    public IdentityDataServiceImpl(EntityManagerFactory emf)
    {
        this.userDAO = new UserDAO(emf);
        this.userTokenDAO = new UserTokenDAO(emf);
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
    @Override
    public User getUserByFingerprint( String fingerprint )
    {
        try
        {
            if( !Strings.isNullOrEmpty(fingerprint))
                return userDAO.find( fingerprint.toLowerCase() );
        }
        catch ( Exception e )
        {
            LOGGER.error( "Failed getUserByFingerprint", e );
        }

        return null;
    }

    //*****************************
    @Override
    public User getUserByUsername( String username )
    {
        try
        {
            if( !Strings.isNullOrEmpty( username ))
                return userDAO.findByUsername( username.toLowerCase() );
        }
        catch ( Exception e )
        {
            LOGGER.error( "Failed getUserByUsername", e );
        }

        return null;
    }


    //*****************************
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
