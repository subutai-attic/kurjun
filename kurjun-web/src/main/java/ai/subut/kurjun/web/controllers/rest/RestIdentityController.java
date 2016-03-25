package ai.subut.kurjun.web.controllers.rest;


import java.util.List;

import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.web.controllers.BaseController;
import ai.subut.kurjun.web.filter.SecurityFilter;
import ninja.Context;
import ninja.session.FlashScope;
import ninja.session.Session;
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
public class RestIdentityController extends BaseController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( RestIdentityController.class );

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
            LOGGER.info( "est user" );
            return Results.ok().render( user ).json();
        }
        else
        {
            LOGGER.info( "no user" );
            return Results.notFound().text().render( "User not found" );
        }

    }


    public Result getActiveUser( Context context )
    {
        UserSession userSession = (UserSession) context.getAttribute(SecurityFilter.USER_SESSION);

        if ( userSession != null )
        {
            return Results.ok().json().render(userSession.getUser().getKeyFingerprint());
        }

        return Results.notFound().text().render( "Active user not found" );
    }


    //*************************
    public Result addUser( @Param( "key" ) String publicKey )
    {
        User user = identityManagerService.addUser( publicKey );

        if(user != null)
        {
            return Results.ok().render( user.getSignature() ).text();
        }
        else
        {
            return Results.internalServerError();
        }
    }


    //*************************
    public Result authorizeUser(@Param( "fingerprint" ) String fingerprint, @Param( "message" ) String message,
                                FlashScope flashScope )
    {
        User user = identityManagerService.authenticateUser(fingerprint, message);

        if (user != null)
        {
            return Results.ok().render( user.getUserToken().getFullToken() ).text();
        }
        else
        {
            return Results.internalServerError();
        }
    }


    //*************************
    public Result setSystemOwner(@Param( "fingerprint" ) String fingerprint, @Param( "key" ) String key, FlashScope flashScope )
    {
        User user = identityManagerService.setSystemOwner(fingerprint,key);

        if (user != null)
        {
            return Results.ok().render( user.getKeyFingerprint() ).json();
        }
        else
        {
            return Results.internalServerError();
        }
    }


    //*************************
    public Result getSystemOwner()
    {
        User user = identityManagerService.getSystemOwner();

        if (user != null)
        {
            return Results.ok().render( user.getKeyFingerprint() ).json();
        }
        else
        {
            return Results.internalServerError();
        }
    }
}
