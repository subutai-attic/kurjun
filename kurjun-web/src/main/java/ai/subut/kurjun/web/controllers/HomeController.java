package ai.subut.kurjun.web.controllers;


import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.web.security.AuthorizedUser;
import ninja.Result;
import ninja.Results;
import ninja.session.FlashScope;
import ninja.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HomeController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger( IdentityController.class );

    public Result homePage( Session session, FlashScope flashScope )
    {
        return Results.ok();
    }
}
