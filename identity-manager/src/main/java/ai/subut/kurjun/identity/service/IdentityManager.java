package ai.subut.kurjun.identity.service;


import ai.subut.kurjun.model.identity.User;


/**
 *
 */
public interface IdentityManager
{
    //********************************************
    User authenticateUser( String userName, String password );

    //********************************************
    User authenticateByToken( String token );
}
