package ai.subut.kurjun.web.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import ninja.Result;
import ninja.Results;
import ninja.session.FlashScope;
import ninja.session.Session;


@Singleton
public class HomeController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger( IdentityController.class );

    public Result homePage( Session session, FlashScope flashScope )
    {
        return Results.ok();
    }
}
