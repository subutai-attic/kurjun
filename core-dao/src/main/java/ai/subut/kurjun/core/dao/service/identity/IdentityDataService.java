package ai.subut.kurjun.core.dao.service.identity;


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
    User mergeUser( User user );


    //*****************************
    User getUser( String fingerprint );


    //*****************************
    List<User> getAllUsers();
}
