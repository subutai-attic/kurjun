package ai.subut.kurjun.web.controllers.rest;


import java.util.List;

import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.web.controllers.BaseController;
import ai.subut.kurjun.web.filter.SecurityFilter;
import ai.subut.kurjun.web.security.AuthorizedUser;
import ninja.Context;
import ninja.session.FlashScope;
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
            return Results.ok().render( user.getKeyFingerprint() ).json();
        }
        else
        {
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
    public Result addUser( @Param( "username" ) String userName, @Param( "key" ) String publicKey )
    {
        try
        {
            User user = identityManagerService.addUser( userName,publicKey );

            if ( user != null )
            {
                return Results.ok().render( user.getSignature() ).text();
            }
            else
            {
                return Results.badRequest().text().render( "Failed to add user's key" );
            }
        }
        catch ( Exception e )
        {
            LOGGER.error( "Failed to add user: "+e.getMessage() );
            return Results.internalServerError().text().render( e.getMessage() );
        }
    }


    //*************************
    public Result authorizeUser(@Param( "fingerprint" ) String fingerprint, @Param( "message" ) String message,
                                Context context, FlashScope flashScope )
    {
        try
        {
            User user = identityManagerService.authenticateUser( fingerprint, message );

            if ( user != null )
            {
                context.getSession().put( SecurityFilter.USER_SESSION, user.getUserToken().getFullToken() );
                return Results.ok().render( user.getUserToken().getFullToken() ).text();
            }
            else
            {
                return Results.notFound().text().render( "User not found" );
            }
        }
        catch ( Exception e )
        {
            LOGGER.error( "Failed to authorize user: "+e.getMessage() );
            return Results.badRequest().text().render( e.getMessage() );
        }
    }


    //*************************
    public Result logout( @AuthorizedUser UserSession userSession, Context context )
    {
        User user = userSession.getUser();
        if ( user != null )
        {
            context.setAttribute( SecurityFilter.USER_TOKEN, null );
            context.setAttribute( SecurityFilter.USER_SESSION, null );
            context.getSession().clear();

            identityManagerService.logout( user );
            return Results.ok();
        }
        else return Results.notFound();
    }


    //*************************
    public Result setSystemOwner(@Param( "fingerprint" ) String fingerprint, @Param( "key" ) String key )
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
