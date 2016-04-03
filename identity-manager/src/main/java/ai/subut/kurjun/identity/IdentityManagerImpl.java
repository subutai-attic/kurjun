package ai.subut.kurjun.identity;


import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.time.DateUtils;

import ai.subut.kurjun.core.dao.model.identity.UserEntity;
import ai.subut.kurjun.core.dao.model.identity.UserTokenEntity;
import ai.subut.kurjun.core.dao.service.identity.IdentityDataService;
import ai.subut.kurjun.identity.service.IdentityManager;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.identity.service.RelationManager;
import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.identity.UserToken;
import ai.subut.kurjun.model.identity.UserType;
import ai.subut.kurjun.security.manager.service.SecurityManager;
import ai.subut.kurjun.security.manager.utils.pgp.PGPKeyUtil;
import ai.subut.kurjun.security.manager.utils.token.TokenUtils;


/**
 *
 */
@Singleton
public class IdentityManagerImpl implements IdentityManager
{

    private static final Logger LOGGER = LoggerFactory.getLogger( IdentityManagerImpl.class );
    public  static final String PUBLIC_USER_ID = "public-user";
    public  static final String PUBLIC_USER_NAME = "public";
    public  static final String SYSTEM_USER_NAME = "subutai";

    public  static final int TOKEN_TTL = 180; // minutes

    //***************************
    private IdentityDataService identityDataService = null;
    private SecurityManager securityManager = null;
    private RelationManager relationManager = null;

    //***************************
    @Inject
    public IdentityManagerImpl( IdentityDataService identityDataService,
                                SecurityManager securityManager,
                                RelationManager relationManager)
    {
        this.identityDataService = identityDataService;
        this.securityManager = securityManager;
        this.relationManager = relationManager;

        //createDefaultUsers();
    }


    //********************************************
    private void createDefaultUsers()
    {
        if ( getUser( PUBLIC_USER_ID ) == null )
        {
            User publicUser = addUser("public", PUBLIC_USER_ID, UserType.System.getId() );
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
        User user = getUser( PUBLIC_USER_ID );

        if(user == null)
        {
            user = addUser("public", PUBLIC_USER_ID, UserType.System.getId() );
        }

        return user;
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
            User user = getPublicUser();
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
                LOGGER.debug( " ******* Successfully logged user:" ,user.getKeyFingerprint() );
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

                //*************
                if(user.getUserToken() == null)
                {
                    user.setUserToken( uToken );
                    identityDataService.mergeUser(  user );
                }
                else
                {
                    identityDataService.mergeToken( uToken );
                }
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
                if ( securityManager.verifyJWT( token, user.getUserToken().getSecret() ) )
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
            return identityDataService.getUser( fingerprint );
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
                user = addUser(SYSTEM_USER_NAME, publicKeyASCII, UserType.RegularOwner.getId() );
            }
            else
            {
                user = getUser( fingerprint );

                if ( user != null )
                {
                    user.setType( UserType.RegularOwner.getId() );

                    //***************************
                    identityDataService.mergeUser( user );
                    //***************************
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
    public User addUser( String userName, String publicKeyASCII )
    {
        return addUser(userName, publicKeyASCII, UserType.Regular.getId() );
    }


    //********************************************
    @Override
    public User addUser( String userName, String publicKeyASCII, int userType )
    {
        User user = null;

        try
        {
            if ( userType == UserType.System.getId() )
            {
                user = new UserEntity();

                user.setUserName( "public" );
                user.setKeyFingerprint( publicKeyASCII );
                user.setType( UserType.System.getId() );
            }
            else
            {
                //*************************
                if ( checkUserName( userName ) == 0 && !Strings.isNullOrEmpty( publicKeyASCII ) )
                {
                    PGPPublicKeyRing pubKeyRing = securityManager.readPGPKeyRing( publicKeyASCII );

                    user = new UserEntity(  );

                    user.setUserName( userName );
                    user.setKeyFingerprint( PGPKeyUtil.getFingerprint( pubKeyRing.getPublicKey().getFingerprint() ) );
                    user.setKeyData( pubKeyRing.getEncoded() );
                    user.setEmailAddress( securityManager.parseEmailAddress( pubKeyRing.getPublicKey() ) );
                    user.setSignature( UUID.randomUUID().toString() );

                    user.setType( UserType.Regular.getId() );
                }
                else
                {
                    LOGGER.info( " ***** Error adding user invalid public key" );
                }
            }

            //******************************
            if ( user != null )
            {
                identityDataService.persistUser( user );
            }
            //******************************
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
    public List<User> getAllUsers()
    {
        try
        {
            return identityDataService.getAllUsers();
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Error getting user list:", ex );
            return Collections.emptyList();
        }
    }


    //********************************************
    @Override
    public UserToken createUserToken( User user, String token, String secret, String issuer, Date validDate )
    {

        UserToken userToken = new UserTokenEntity();

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
            validDate = DateUtils.addMinutes( new Date( System.currentTimeMillis() ), TOKEN_TTL );
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


    //********************************************
    @Override
    public int checkUserName( String userName )
    {
        if(Strings.isNullOrEmpty( userName ))
            return 1;
        else
        {
            if(userName.length()<3)
                return 2;
            else if(userName.toLowerCase().equals( "public" ) || userName.toLowerCase().equals( "admin" ))
                return 3;
            else
                return 0;

        }
    }
    //********************************************
}