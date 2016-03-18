package ai.subut.kurjun.identity.service;


import java.util.Date;
import java.util.List;

import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserToken;


/**
 *
 */
public interface IdentityManager
{
    //********************************************
    User authenticateUser( String fingerprint, String password, int authType );

    //********************************************
    User authenticateByToken( String token, String sharedSecret );

    //********************************************
    User getUser( String fingerprint );

    //********************************************
    User addUser( String publicKeyASCII );

    //********************************************
    List<User> getAllUsers();

    //********************************************
    UserToken createUserToken( User user, String token, String secret, String issuer, Date validDate );
}
