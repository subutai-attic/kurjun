package ai.subut.kurjun.web.conf;


import ai.subut.kurjun.web.conf.routes.rest.RestRoutes;
import ai.subut.kurjun.web.controllers.DownloadController;
import ai.subut.kurjun.web.controllers.HomeController;
import ai.subut.kurjun.web.controllers.IdentityController;
import ai.subut.kurjun.web.controllers.TemplateController;
import ai.subut.kurjun.web.controllers.rest.*;
import com.google.inject.Inject;
import ninja.Router;
import ninja.application.ApplicationRoutes;


/**
 *
 */

public class Routes implements ApplicationRoutes
{
    @Inject
    private RestRoutes restRoutes;

    @Override
    public void init( final Router router )
    {
        restRoutes.init( router );


        // -------------------------------------------------------------------------------------------------------------
        //  Assets (CSS, JS, Images & Icons)
        // -------------------------------------------------------------------------------------------------------------
        router.GET().route( "/assets/{fileName: .*}" ).with( DownloadController.class, "serveStatic" );

        // -------------------------------------------------------------------------------------------------------------
        //  Home
        // -------------------------------------------------------------------------------------------------------------
        router.GET().route( "/" ).with( HomeController.class, "homePage" );

        // -------------------------------------------------------------------------------------------------------------
        //  Identity
        // -------------------------------------------------------------------------------------------------------------
        router.POST().route( "/login" ).with( IdentityController.class, "authorizeUser" );
        router.GET().route( "/login" ).with( IdentityController.class, "loginPage" );

        // -------------------------------------------------------------------------------------------------------------
        //  Templates
        // -------------------------------------------------------------------------------------------------------------
        //router.GET().route( "/templates" ).with( TemplateController.class, "listTemplates" );
        router.POST().route( "/templates" ).with( TemplateController.class, "uploadTemplate" );
        router.POST().route( "/templates/{id}" ).with( TemplateController.class, "deleteTemplate" );

    }
}
