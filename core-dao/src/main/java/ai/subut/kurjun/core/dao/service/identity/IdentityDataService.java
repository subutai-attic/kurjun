package ai.subut.kurjun.core.dao.service.identity;


import java.util.List;

import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserToken;


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
    User getUserByFingerprint( String fingerprint );

    //*****************************
    User getUserByUsername( String username );


    //*****************************
    List<User> getAllUsers();


    //*****************************
    List<UserToken> getAllTokens();


    //*****************************
    UserToken  mergeUserToken( UserToken userToken );


    //*****************************
    UserToken getUserToken( String fingerprint );
}
