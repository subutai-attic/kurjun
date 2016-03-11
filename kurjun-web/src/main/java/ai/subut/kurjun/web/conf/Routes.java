package ai.subut.kurjun.web.conf;


import ai.subut.kurjun.web.controllers.AptController;
import ai.subut.kurjun.web.controllers.TemplateController;
import ninja.Router;
import ninja.application.ApplicationRoutes;


/**
 *
 */

public class Routes implements ApplicationRoutes
{
    private static final String baseUrl = "/rest/kurjun/v1/";
    private static final String baseTemplateUrl = baseUrl + "template/";
    private static final String baseDebUrl = baseUrl + "deb/";


    @Override
    public void init( final Router router )
    {
        //REST Template Controller

        router.GET().route( baseTemplateUrl + "all" ).with( TemplateController.class, "list" );
        router.GET().route( baseTemplateUrl ).with( TemplateController.class, "download" );
        router.POST().route( baseTemplateUrl ).with( TemplateController.class, "upload" );
        router.DELETE().route( baseTemplateUrl ).with( TemplateController.class, "delete" );

        //REST APT Controller
        router.GET().route( baseDebUrl + "dists/{release}/Release" ).with( AptController.class, "release" );
        router.GET()
              .route( baseDebUrl + "dists/{release}/{component}/{arch: binary-\\w+}/{packages: Packages(\\.\\w+)?}" )
              .with( AptController.class, "packageIndexes" );
        router.GET().route( baseDebUrl + "pool/{filename: .+}" )
              .with( TemplateController.class, "getPackageByFileName" );
        router.GET().route( baseDebUrl + "info" ).with( AptController.class, "info" );
        router.GET().route( baseDebUrl + "get" ).with( AptController.class, "download" );
        router.GET().route( baseDebUrl + "list" ).with( AptController.class, "list" );
        router.POST().route( baseDebUrl + "upload" ).with( AptController.class, "upload" );
        router.DELETE().route( baseDebUrl + "delete" ).with( AptController.class, "delete" );
        ;
    }
}
