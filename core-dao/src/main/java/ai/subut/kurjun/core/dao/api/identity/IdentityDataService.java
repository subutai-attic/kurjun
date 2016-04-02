package ai.subut.kurjun.core.dao.api.identity;


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
    void mergeUser( User user );

    //*****************************
    User getUser( String fingerprint );

    //*****************************
    List<User> getAllUsers();
}
