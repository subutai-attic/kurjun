package ai.subut.kurjun.web.conf;


import ai.subut.kurjun.web.controllers.rest.template.RestTemplateController;
import ninja.Router;
import ninja.application.ApplicationRoutes;


/**
 *
 */
public class Routes  implements ApplicationRoutes
{

    @Override
    public void init( final Router router )
    {
        // REST Template Controller

        router.GET().route( "/rest/kurjun/{repository}/get" ).with( RestTemplateController.class, "start" );
        router.GET().route( "/rest/kurjun/{repositories}/" ).with( RestTemplateController.class, "start" );
        router.GET().route( "/rest/kurjun/{repository}/get" ).with( RestTemplateController.class, "start" );
        router.GET().route( "/rest/kurjun/{repository}/info" ).with( RestTemplateController.class, "start" );
        router.GET().route( "/rest/kurjun/{repository}/list" ).with( RestTemplateController.class, "start" );
        router.GET().route( "/rest/kurjun/upload/{repository" ).with( RestTemplateController.class, "start" );
        router.GET().route( "/rest/kurjun/{repository}" ).with( RestTemplateController.class, "start" );
    }


}
