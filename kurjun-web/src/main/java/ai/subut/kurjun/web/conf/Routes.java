package ai.subut.kurjun.web.conf;


import ai.subut.kurjun.web.conf.routes.rest.RestRoutes;
import ai.subut.kurjun.web.controllers.DownloadController;
import ai.subut.kurjun.web.controllers.IdentityController;
import ai.subut.kurjun.web.controllers.TemplateController;
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
        //router.GET().route( "/" ).with( HomeController.class, "homePage" );

        // -------------------------------------------------------------------------------------------------------------
        //  Identity
        // -------------------------------------------------------------------------------------------------------------
        router.POST().route( "/login" ).with( IdentityController.class, "authorizeUser" );
        router.GET().route( "/login" ).with( IdentityController.class, "loginPage" );
        router.POST().route( "/users" ).with( IdentityController.class, "createUser" );
        router.GET().route( "/users" ).with( IdentityController.class, "listUsers" );
        router.POST().route( "/logout" ).with( IdentityController.class, "logout" );
        router.POST().route( "/system/owner" ).with( IdentityController.class, "setSystemOwner");
        router.GET().route( "/system/owner" ).with( IdentityController.class, "getSystemOwner");

        // -------------------------------------------------------------------------------------------------------------
        //  Templates
        // -------------------------------------------------------------------------------------------------------------
        router.GET().route( "/" ).with( TemplateController.class, "listTemplates" );
        router.GET().route( "/templates/upload" ).with( TemplateController.class, "getUploadTemplateForm" );
        router.POST().route( "/templates" ).with( TemplateController.class, "uploadTemplate" );
        router.GET().route( "/templates/{id}/info" ).with( TemplateController.class, "getTemplateInfo" );
        router.GET().route( "/templates/{id}/download" ).with( TemplateController.class, "downloadTemplate" );
        router.POST().route( "/templates/{id}" ).with( TemplateController.class, "deleteTemplate" );

    }
}
