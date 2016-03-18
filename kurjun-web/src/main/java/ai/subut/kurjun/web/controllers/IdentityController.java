package ai.subut.kurjun.web.controllers;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;


/**
 * REST Controller for Identity Management
 */
@Singleton
public class IdentityController extends BaseController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( IdentityController.class );

    @Inject
    IdentityManagerService identityManagerService;


    //*************************
    public Result getUsers()
    {
        List<User> users = identityManagerService.getAllUsers();

        return Results.ok().render( users ).json();
    }


    //*************************
    public Result getUser( @Param( "fingerprint" ) String fingerprint )
    {
        User user = identityManagerService.getUser( fingerprint );

        if(user != null)
        {
            return Results.ok().render( user ).json();
        }
        else
        {
            return Results.notFound();
        }

    }


    //*************************
    public Result addUser( @Param( "key" ) String publicKey )
    {
        User user = identityManagerService.addUser( publicKey );

        if(user != null)
        {
            return Results.ok().render( user ).json();
        }
        else
        {
            return Results.internalServerError();
        }
    }


    //*************************
    public Result authorizeUser( @Param( "fingerprint" ) String fingerprint, @Param( "key" ) String publicKey  )
    {
        User user = identityManagerService.addUser( publicKey );

        if(user != null)
        {
            return Results.ok().render( user.getUserToken().getFullToken() ).json();
        }
        else
        {
            return Results.internalServerError();
        }
    }

}
