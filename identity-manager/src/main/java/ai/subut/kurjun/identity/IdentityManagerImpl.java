package ai.subut.kurjun.identity;


import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.identity.service.FileDbProvider;
import ai.subut.kurjun.identity.service.IdentityManager;

import com.google.common.base.Strings;
import com.google.inject.Inject;

import ai.subut.kurjun.identity.service.RelationManager;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.security.manager.service.SecurityManager;


/**
 *
 */
public class IdentityManagerImpl implements IdentityManager
{

    private static final Logger LOGGER = LoggerFactory.getLogger( IdentityManagerImpl.class );

    //***************************
    @Inject
    private SecurityManager securityManager;

    @Inject
    private RelationManager relationManager;

    @Inject
    private FileDbProvider fileDbProvider;


    //***************************
    public IdentityManagerImpl()
    {

    }


    //********************************************
    @Override
    public User authenticateUser( String fingerprint, String authMessage, int authType )
    {
        User user = getUser( fingerprint );

        if(user != null)
        {
            if(securityManager.verifyPGPSignature( authMessage,user.getKeyData() ))
            {

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
    public User authenticateByToken( String token)
    {
        return null;
    }


    //********************************************
    @Override
    public User getUser( String fingerprint )
    {
        try
        {
            FileDb fileDb = fileDbProvider.get();
            return fileDb.get( DefaultUser.MAP_NAME, fingerprint.toLowerCase(), DefaultUser.class );
        }
        catch(Exception ex)
        {
            LOGGER.error( " ***** Error getting user with fingerprint:" + fingerprint,ex);
            return null;
        }
    }


    //********************************************
    @Override
    public User addUser( String publicKeyASCII )
    {
        User user = null;

        try
        {
            if ( !Strings.isNullOrEmpty( publicKeyASCII) )
            {
                user = new DefaultUser( securityManager.readPGPKey( publicKeyASCII ) );

                FileDb fileDb = fileDbProvider.get();
                fileDb.put( DefaultUser.MAP_NAME, user.getKeyFingerprint().toLowerCase(), user );
            }
            else
            {
                LOGGER.info( " ***** Error adding user invalid public key");
            }
        }
        catch(Exception ex)
        {
            LOGGER.error( " ***** Error adding user:",ex);
            return null;
        }

        return user;
    }


    //********************************************
    @Override
    public List<User> getAllUsers()
    {
        return null;
    }


    //********************************************
    @Override
    public User createJWTToken(User user)
    {
        String header;
        String claim;
        String sharedSecret = UUID.randomUUID().toString();

        user.setSharedSecret( sharedSecret );

        securityManager.createJWToken( header,claim,sharedSecret  );
    }


}
