package ai.subut.kurjun.web.controllers;


import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.web.filter.SecurityFilter;
import ai.subut.kurjun.web.security.AuthorizedUser;
import ai.subut.kurjun.web.service.IdentityManagerService;
import com.google.inject.Inject;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.session.FlashScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class IdentityController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger( IdentityController.class );

    @Inject
    IdentityManagerService identityManagerService;



    //*************************
    public Result loginPage( Context context )
    {
        return Results.html().template("views/login.ftl");
    }


    //*************************
    public Result authorizeUser(@Param( "fingerprint" ) String fingerprint, @Param( "message" ) String message,
                                Context context, FlashScope flashScope )
    {
        User user = identityManagerService.authenticateUser(fingerprint, message);

        if (user != null)
        {
            context.setAttribute( SecurityFilter.USER_TOKEN, user.getUserToken().getFullToken() );
            return Results.redirect("/");
        }
        else
        {
            flashScope.error("Failed to authorize.");
            return Results.redirect("/login");
        }
    }

    public Result createUser( @AuthorizedUser UserSession userSession, @Param( "key" ) String publicKey, FlashScope flashScope )
    {
        User user = identityManagerService.addUser( publicKey );

        if(user != null)
        {
            flashScope.success( "User created successfully" );
            return Results.redirect( "/users" );
        }
        else
        {
            flashScope.error( "Failed to create user.");
            return Results.redirect( "/users" );
        }
    }


    public Result listUsers( @AuthorizedUser UserSession userSession )
    {
        List<User> users = identityManagerService.getAllUsers();

        return Results.html().template("views/users.ftl").render("users", users);
    }


    public Result logout( Context context )
    {
        context.setAttribute(SecurityFilter.USER_TOKEN, null);
        return Results.redirect("/");
    }


}
