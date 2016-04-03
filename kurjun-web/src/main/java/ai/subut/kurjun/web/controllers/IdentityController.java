package ai.subut.kurjun.web.controllers;


import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.web.filter.SecurityFilter;
import ai.subut.kurjun.web.service.IdentityManagerService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.session.FlashScope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@Singleton
public class IdentityController extends BaseController
{

    private static final Logger LOGGER = LoggerFactory.getLogger( IdentityController.class );

    @Inject
    IdentityManagerService identityManagerService;


    //*************************
    public Result loginPage( Context context )
    {
        return Results.html().template( "views/login.ftl" );
    }


    //*************************
    public Result authorizeUser( @Param( "fingerprint" ) String fingerprint, @Param( "message" ) String message,
                                 Context context, FlashScope flashScope )
    {
        User user = identityManagerService.authenticateUser( fingerprint, message );

        if ( user != null )
        {
            context.getSession().put( SecurityFilter.USER_SESSION, user.getUserToken().getFullToken() );
            return Results.redirect( context.getContextPath() + "/" );
        }
        else
        {
            flashScope.error( "Failed to authorize." );
            return Results.redirect( context.getContextPath() + "/login" );
        }
    }


    public Result createUser( @Param( "username" ) String userName, @Param( "key" ) String publicKey, Context context,
                              FlashScope flashScope )
    {
        User user = identityManagerService.addUser(userName, publicKey );

        if ( user != null )
        {
            return Results.html().template( "views/token.ftl" ).render( "token", user.getSignature() );
        }
        else
        {
            flashScope.error( "Failed to create user." );
            return Results.redirect( context.getContextPath() + "/users" );
        }
    }


    public Result listUsers()
    {
        List<User> users = identityManagerService.getAllUsers();
        User sysOwner = identityManagerService.getSystemOwner();

        return Results.html().template( "views/users.ftl" ).render( "users", users ).render( "sys_owner", sysOwner );
    }


    public Result logout( Context context )
    {
        context.setAttribute( SecurityFilter.USER_TOKEN, null );
        context.setAttribute( SecurityFilter.USER_SESSION, null );
        context.getSession().clear();
        return Results.redirect( context.getContextPath() + "/" );
    }


    public Result setSystemOwner( @Param( "key" ) String key, @Param( "fingerprint" ) String fingerprint,
                                  Context context, FlashScope flashScope )
    {
        if ( identityManagerService.getSystemOwner() == null )
        {
            User user = identityManagerService.setSystemOwner( fingerprint, key );

            if ( user != null )
            {
                flashScope.success( "System owner set successfully. MessageId:" + user.getSignature() );
            }
            else
            {
                flashScope.error( "Couldn't find user by fingerprint." );
            }
        }
        else
        {
            flashScope.error( "System owner has been set already." );
        }

        return Results.redirect( context.getContextPath() + "/users" );
    }


    public Result getSystemOwner( Context context, FlashScope flashScope )
    {
        User user = identityManagerService.getSystemOwner();

        if ( user != null )
        {
            LOGGER.info( "owner found" );
            return Results.html().template( "views/_popup-view-system-owner.ftl" ).render( "sys_owner", user );
        }
        else
        {
            LOGGER.info( "ownwer NOT found" );
            return Results.html().template( "views/_popup-view-system-owner.ftl" ).render( "sys_owner", user );
        }
    }
}
