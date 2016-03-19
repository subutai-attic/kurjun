package ai.subut.kurjun.web.controllers;


import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.web.service.IdentityManagerService;
import com.google.inject.Inject;
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
    public Result loginPage()
    {
        return Results.html().template("views/login.ftl");
    }


    //*************************
    public Result authorizeUser(@Param( "fingerprint" ) String fingerprint, @Param( "message" ) String message,
                                FlashScope flashScope )
    {
        User user = identityManagerService.authenticateUser(fingerprint, message);
        // TODO: create session
        if (user != null)
        {
            return Results.redirect("/");
        }
        else
        {
            flashScope.error("Failed to authorize.");
            return Results.redirect("/login");
        }
    }

    public Result createUser( @Param( "key" ) String publicKey, FlashScope flashScope )
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


    public Result listUsers()
    {
        List<User> users = identityManagerService.getAllUsers();

        return Results.html().template("views/users.ftl").render("users", users);
    }


    public Result logout()
    {
        // TODO: clear session
        return Results.redirect("/login");
    }


}
