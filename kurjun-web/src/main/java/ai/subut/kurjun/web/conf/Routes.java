package ai.subut.kurjun.web.conf;


import ai.subut.kurjun.web.controllers.RestTemplateController;
import ninja.Router;
import ninja.application.ApplicationRoutes;


/**
 *
 */

public class Routes implements ApplicationRoutes
{

    @Override
    public void init( final Router router )
    {
        //REST Template Controller

        router.GET().route( "/rest/kurjun/v1/all" ).with( RestTemplateController.class, "list" );
        router.GET().route( "/rest/kurjun/v1/" ).with( RestTemplateController.class, "download" );
        router.POST().route( "/rest/kurjun/v1/" ).with( RestTemplateController.class, "upload" );
        router.DELETE().route( "/rest/kurjun/v1/" ).with( RestTemplateController.class, "delete" );

        //REST APT Controller

    }
}
