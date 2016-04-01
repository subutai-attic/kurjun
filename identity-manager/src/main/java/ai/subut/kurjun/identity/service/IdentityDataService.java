package ai.subut.kurjun.identity.service;


import java.util.List;

import ai.subut.kurjun.model.identity.User;


/**
 *
 */
public interface IdentityDataService
{
    //*****************************
    void persistUser( User user );


    //*****************************
    User getUser( String fingerprint );

    //*****************************
    List<User> getAllUsers();
}
