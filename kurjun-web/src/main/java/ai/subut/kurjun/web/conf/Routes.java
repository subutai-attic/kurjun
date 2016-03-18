package ai.subut.kurjun.web.conf;


import ai.subut.kurjun.web.controllers.AliquaController;
import ai.subut.kurjun.web.controllers.AptController;
import ai.subut.kurjun.web.controllers.IdentityController;
import ai.subut.kurjun.web.controllers.TemplateController;
import ninja.Router;
import ninja.application.ApplicationRoutes;


/**
 *
 */

public class Routes implements ApplicationRoutes
{
    private static final String baseUrl = "/rest/";

    private static final String baseTemplateUrl = baseUrl + "template/";

    private static final String baseDebUrl = baseUrl + "deb/";

    private static final String baseRawUrl = baseUrl + "file/";

    private static final String baseIdentityUrl = baseUrl + "identity/";

    private static final String baseSecurityUrl = baseUrl + "security/";

    @Override
    public void init( final Router router )
    {
        //REST Template Controller

        router.GET().route( baseTemplateUrl + "list" ).with( TemplateController.class, "list" );
        router.GET().route( baseTemplateUrl + "get" ).with( TemplateController.class, "download" );
        router.GET().route( baseTemplateUrl + "md5" ).with( TemplateController.class, "md5" );
        router.GET().route( baseTemplateUrl + "info" ).with( TemplateController.class, "info" );
        router.POST().route( baseTemplateUrl + "upload" ).with( TemplateController.class, "upload" );
        router.DELETE().route( baseTemplateUrl + "delete" ).with( TemplateController.class, "delete" );

        //REST APT Controller
        router.GET().route( baseDebUrl + "dists/{release}/Release" ).with( AptController.class, "release" );
        router.GET()
              .route( baseDebUrl + "dists/{release}/{component}/{arch: binary-\\w+}/{packages: Packages(\\.\\w+)?}" )
              .with( AptController.class, "packageIndexes" );
        router.GET().route( baseDebUrl + "pool/{filename: .+}" ).with( AptController.class, "getPackageByFileName" );
        router.GET().route( baseDebUrl + "info" ).with( AptController.class, "info" );
        router.GET().route( baseDebUrl + "md5" ).with( AptController.class, "md5" );
        router.GET().route( baseDebUrl + "get" ).with( AptController.class, "download" );
        router.GET().route( baseDebUrl + "list" ).with( AptController.class, "list" );
        router.POST().route( baseDebUrl + "upload" ).with( AptController.class, "upload" );
        router.DELETE().route( baseDebUrl + "delete" ).with( AptController.class, "delete" );

        //REST Raw file Controller
        router.GET().route( baseRawUrl + "get" ).with( AliquaController.class, "getFile" );
        router.GET().route( baseRawUrl + "md5" ).with( AliquaController.class, "md5" );
        router.GET().route( baseRawUrl + "list" ).with( AliquaController.class, "getList" );
        router.GET().route( baseRawUrl + "info" ).with( AliquaController.class, "info" );
        router.POST().route( baseRawUrl + "upload" ).with( AliquaController.class, "upload" );
        router.DELETE().route( baseRawUrl + "delete" ).with( AliquaController.class, "delete" );

        //REST Identity Controller
        router.GET().route( baseIdentityUrl + "user/list" ).with( IdentityController.class, "getUsers" );
        router.GET().route( baseIdentityUrl + "user/get" ).with( IdentityController.class, "getUser" );
        router.POST().route( baseIdentityUrl + "user/add" ).with( IdentityController.class, "addUser" );
        router.GET().route( baseIdentityUrl + "user/authz" ).with( IdentityController.class, "authorizeUser" );

        //REST Security Controller
        //router.GET().route( baseSecurityUrl + "keyman" ).with( IdentityController.class, "getUsers" );

    }
}
