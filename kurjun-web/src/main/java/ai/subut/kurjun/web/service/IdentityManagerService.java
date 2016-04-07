package ai.subut.kurjun.web.service;


import java.util.List;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserSession;


/**
 *
 */
public interface IdentityManagerService extends BaseService
{
    //*************************************
    List<User> getAllUsers();


    //*************************************
    String getPublicUserId();


    //*************************************
    String getPublicUserName();

    //*************************************
    boolean isPublicUser( User user );

    //*************************************
    User getPublicUser();

    //*************************************
    User getUser( String userId );


    //*************************************
    User addUser(String userName, String publicKeyASCII );


    //*************************************
    User authenticateUser( String fingerprint, String authzMessage );


    //*************************************
    UserSession loginUser( String fingerprint, String authzMessage );


    //*************************************
    UserSession loginPublicUser();


    //*************************************
    User setSystemOwner( String fingerprint, String publicKeyASCII );


    //*************************************
    User getSystemOwner();

    void logout(User user);

}
