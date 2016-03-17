package ai.subut.kurjun.web.service;


import java.util.List;

import ai.subut.kurjun.model.identity.User;


/**
 *
 */
public interface IdentityManagerService
{
    //*************************************
    List<User> getAllUsers();

    //*************************************
    User getUser( String userId );
}
