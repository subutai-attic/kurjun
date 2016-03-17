package ai.subut.kurjun.identity.service;


import java.util.List;

import ai.subut.kurjun.model.identity.User;


/**
 *
 */
public interface IdentityManager
{
    //********************************************
    User authenticateUser( String fingerprint, String password, int authType );

    //********************************************
    User authenticateByToken( String token );

    //********************************************
    User getUser( String fingerprint );

    //********************************************
    User addUser( String publicKeyASCII );

    //********************************************
    List<User> getAllUsers();
}
