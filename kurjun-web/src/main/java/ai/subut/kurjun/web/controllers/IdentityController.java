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


public class IdentityController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger( IdentityController.class );

    @Inject
    IdentityManagerService identityManagerService;



    //*************************
    public Result loginPage()
    {
        LOGGER.info("login page");
        return Results.html().template("views/login.ftl");
    }


    //*************************
    public Result authorizeUser(@Param( "fingerprint" ) String fingerprint, @Param( "message" ) String message,
                                FlashScope flashScope )
    {
        User user = identityManagerService.authenticateUser(fingerprint, message);

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

}
