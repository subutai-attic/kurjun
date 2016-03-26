package ai.subut.kurjun.identity;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.time.DateUtils;

import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.identity.service.FileDbProvider;
import ai.subut.kurjun.identity.service.IdentityManager;

import com.google.common.base.Strings;
import com.google.inject.Inject;

import ai.subut.kurjun.identity.service.RelationManager;
import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.identity.UserToken;
import ai.subut.kurjun.model.identity.UserType;
import ai.subut.kurjun.security.manager.service.SecurityManager;
import ai.subut.kurjun.security.manager.utils.token.TokenUtils;


/**
 *
 */
public class IdentityManagerImpl implements IdentityManager
{

    private static final Logger LOGGER = LoggerFactory.getLogger( IdentityManagerImpl.class );
    public  static final String PUBLIC_USER_ID = "public-user";


    private FileDbProvider fileDbProvider;
    //***************************
    @Inject
    private SecurityManager securityManager;

    @Inject
    private RelationManager relationManager;



    //***************************
    @Inject
    public IdentityManagerImpl( FileDbProvider fileDbProvider )
    {
        this.fileDbProvider = fileDbProvider;

        createDefaultUsers();
    }


    //********************************************
    private void createDefaultUsers()
    {
        if ( getUser( PUBLIC_USER_ID ) == null )
        {
            User publicUser = addUser( PUBLIC_USER_ID, UserType.System.getId() );
        }
    }


    //********************************************
    @Override
    public RelationManager getRelationManager()
    {
        return relationManager;
    }


    //********************************************
    @Override
    public User getPublicUser()
    {
        return getUser( PUBLIC_USER_ID );
    }


    //********************************************
    @Override
    public String getPublicUserId()
    {
        return PUBLIC_USER_ID;
    }


    //********************************************
    @Override
    public UserSession loginPublicUser()
    {
        try
        {
            User user = getUser( PUBLIC_USER_ID );
            UserSession userSession = new DefaultUserSession();
            userSession.setUser( user );


            return userSession;
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ******* Error in IdentityManager" ,ex );
            return null;
        }
    }


    //********************************************
    @Override
    public UserSession loginUser( String fingerprint, String authMessage )
    {
        try
        {
            User user = null;

            if ( fingerprint.equals( "token" ) )
            {
                user = authenticateByToken( authMessage );
            }
            else
            {
                user = authenticateUser( fingerprint, authMessage );
            }


            if ( user != null )
            {
                UserSession userSession = new DefaultUserSession();
                userSession.setUser( user );
                userSession.setUserToken( user.getUserToken() );

                //*****************************************
                LOGGER.info( " ******* Successfully logged user:" ,user.getKeyFingerprint() );
                //*****************************************

                return userSession;
            }
            else
            {
                return null;
            }
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ******* Error in IdentityManager" ,ex );
            return null;
        }
    }


    //********************************************
    @Override
    public User authenticateUser( String fingerprint, String authMessage )
    {
        User user = getUser( fingerprint );

        if ( user != null )
        {
            if ( securityManager.verifyPGPSignatureAndContent( authMessage, user.getSignature(), user.getKeyData() ) )
            {
                UserToken uToken = createUserToken( user, user.getKeyFingerprint(), "", "", null );
                user.setUserToken( uToken );

                //*************
                saveUser( user );
                //*************

                //*****************************************
                LOGGER.info( " ******* Successfully authenticated user:" ,user.getKeyFingerprint() );
                //*****************************************


                return user;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }


    //********************************************
    @Override
    public User authenticateByToken( String token )
    {
        String fingerprint = TokenUtils.getSubject( token );

        User user = getUser( fingerprint );

        if ( user != null )
        {
            if(user.getUserToken() != null)
            {
                if ( securityManager.verifyJWTSignature( token, user.getUserToken().getSecret() ) )
                {
                    return user;
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }


    //********************************************
    @Override
    public User getUser( String fingerprint )
    {
        try
        {
            FileDb fileDb = fileDbProvider.get();
            User user = fileDb.get( DefaultUser.MAP_NAME, fingerprint.toLowerCase(), DefaultUser.class );
            fileDb.close();

            return user;
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Error getting user with fingerprint:" + fingerprint );
            return null;
        }
    }


    //********************************************
    @Override
    public User getSystemOwner()
    {
        try
        {
            List<User> users = getAllUsers();

            if ( users.isEmpty() )
            {
                LOGGER.info( " ***** Owner of the system is not set yet" );
            }
            else
            {
                for ( User user : users )
                {
                    if ( user.getType() == UserType.RegularOwner.getId() )
                    {
                        return user;
                    }
                }
            }
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Error !!! Owner of the system is not set yet", ex );
            return null;
        }

        return null;
    }


    //********************************************
    @Override
    public User setSystemOwner( String fingerprint, String publicKeyASCII )
    {

        if ( getSystemOwner() == null )
        {
            User user = null;

            if ( Strings.isNullOrEmpty( fingerprint ) )
            {
                user = addUser( publicKeyASCII, UserType.RegularOwner.getId() );
            }
            else
            {
                user = getUser( fingerprint );

                if ( user != null )
                {
                    user.setType( UserType.RegularOwner.getId() );
                    saveUser( user );
                }
                else
                {
                    LOGGER.info( " ***** System Owner is not set, user not found with fingeprint:" + fingerprint );
                }
            }

            return user;
        }
        else
        {
            LOGGER.info( " ***** System Owner is already set !!!" );
            return null;
        }
    }


    //********************************************
    @Override
    public User addUser( String publicKeyASCII )
    {
        return addUser( publicKeyASCII, UserType.Regular.getId() );
    }


    //********************************************
    @Override
    public User addUser( String publicKeyASCII, int userType )
    {
        User user = null;

        try
        {
            if ( userType == UserType.System.getId() )
            {
                user = new DefaultUser();
                user.setKeyId( publicKeyASCII );
                user.setKeyFingerprint( publicKeyASCII );
                user.setType( UserType.System.getId() );

            }
            else
            {
                if ( !Strings.isNullOrEmpty( publicKeyASCII ) )
                {
                    user = new DefaultUser( securityManager.readPGPKey( publicKeyASCII ) );
                    user.setType( UserType.Regular.getId() );
                }
                else
                {
                    LOGGER.info( " ***** Error adding user invalid public key" );
                }
            }

            //****************
            if ( user != null )
            {
                saveUser( user );
            }
            //****************
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Error adding user:", ex );
            return null;
        }

        return user;
    }


    //********************************************
    @Override
    public User saveUser( User user )
    {
        try
        {
            FileDb fileDb = fileDbProvider.get();
            fileDb.put( DefaultUser.MAP_NAME, user.getKeyFingerprint().toLowerCase(), user );
            fileDb.close();
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Error saving user:", ex );
            return null;
        }

        return user;
    }


    //********************************************
    @Override
    public List<User> getAllUsers()
    {
        try
        {
            FileDb fileDb = fileDbProvider.get();
            Map<String, User> map = fileDb.get( DefaultUser.MAP_NAME );
            fileDb.close();

            if ( map != null )
            {
                List<User> items = new ArrayList<>( map.values() );

                return items;
            }
            else
            {
                return null;
            }
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Error adding user:", ex );
            return null;
        }
    }


    //********************************************
    @Override
    public UserToken createUserToken( User user, String token, String secret, String issuer, Date validDate )
    {
        UserToken userToken = new DefaultUserToken();

        if ( Strings.isNullOrEmpty( token ) )
        {
            token = UUID.randomUUID().toString();
        }
        if ( Strings.isNullOrEmpty( issuer ) )
        {
            issuer = "io.subutai";
        }
        if ( Strings.isNullOrEmpty( secret ) )
        {
            secret = UUID.randomUUID().toString();
        }
        if ( validDate == null )
        {
            validDate = DateUtils.addMinutes( new Date( System.currentTimeMillis() ), 120 );
        }

        userToken.setToken( token );
        userToken.setHashAlgorithm( "HS256" );
        userToken.setIssuer( issuer );
        userToken.setSecret( secret );
        userToken.setValidDate( validDate );

        return userToken;
    }


    //********************************************
    @Override
    public boolean hasPermmission( User user, RelationObject relationObject, Permission permission )
    {
        return true;
    }
    //********************************************
}