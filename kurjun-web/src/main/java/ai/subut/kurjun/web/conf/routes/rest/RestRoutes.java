package ai.subut.kurjun.web.conf.routes.rest;


import ai.subut.kurjun.web.controllers.rest.RestAliquaController;
import ai.subut.kurjun.web.controllers.rest.RestAptController;
import ai.subut.kurjun.web.controllers.rest.RestIdentityController;
import ai.subut.kurjun.web.controllers.rest.RestRelationController;
import ai.subut.kurjun.web.controllers.rest.RestRepositoryController;
import ai.subut.kurjun.web.controllers.rest.RestTemplateController;
import ninja.Router;
import ninja.application.ApplicationRoutes;


public class RestRoutes implements ApplicationRoutes
{

    private static final String baseUrl = "/rest/";

    private static final String baseTemplateUrl = baseUrl + "template/";

    private static final String baseDebUrl = baseUrl + "deb/";

    private static final String baseRawUrl = baseUrl + "file/";

    private static final String baseIdentityUrl = baseUrl + "identity/";

    private static final String baseSecurityUrl = baseUrl + "security/";

    private static final String baseRepositoryUrl = baseUrl + "repository/";

    private static final String baseRelationsUrl = baseUrl + "relations/";


    @Override
    public void init( Router router )
    {

        router.OPTIONS().route( baseTemplateUrl + "list" ).with( RestTemplateController.class, "list" );

        // REST Template Controller

        router.GET().route( baseTemplateUrl + "list" ).with( RestTemplateController.class, "list" );
        router.GET().route( baseTemplateUrl + "get" ).with( RestTemplateController.class, "download" );
        router.GET().route( baseTemplateUrl + "md5" ).with( RestTemplateController.class, "md5" );
        router.GET().route( baseTemplateUrl + "info" ).with( RestTemplateController.class, "info" );
        router.POST().route( baseTemplateUrl + "upload" ).with( RestTemplateController.class, "upload" );
        router.DELETE().route( baseTemplateUrl + "delete" ).with( RestTemplateController.class, "delete" );

        //REST APT Controller
        router.GET().route( baseDebUrl + "dists/{release}/Release" ).with( RestAptController.class, "release" );
        router.GET().route( baseDebUrl + "dists/{release}/{component}/{arch}/{packages}" )
              .with( RestAptController.class, "packageIndexes" );
        router.GET().route( baseDebUrl + "pool/{filename: .+}" )
              .with( RestAptController.class, "getPackageByFileName" );
        router.GET().route( baseDebUrl + "info" ).with( RestAptController.class, "info" );
        router.GET().route( baseDebUrl + "md5" ).with( RestAptController.class, "md5" );
        router.GET().route( baseDebUrl + "get" ).with( RestAptController.class, "download" );
        router.GET().route( baseDebUrl + "list" ).with( RestAptController.class, "list" );
        router.POST().route( baseDebUrl + "upload" ).with( RestAptController.class, "upload" );
        router.DELETE().route( baseDebUrl + "delete" ).with( RestAptController.class, "delete" );

        //REST Raw file Controller
        router.GET().route( baseRawUrl + "get" ).with( RestAliquaController.class, "getFile" );
        router.GET().route( baseRawUrl + "md5" ).with( RestAliquaController.class, "md5" );
        router.GET().route( baseRawUrl + "list" ).with( RestAliquaController.class, "list" );
        router.GET().route( baseRawUrl + "info" ).with( RestAliquaController.class, "info" );
        router.POST().route( baseRawUrl + "upload" ).with( RestAliquaController.class, "upload" );
        router.DELETE().route( baseRawUrl + "delete" ).with( RestAliquaController.class, "delete" );

        //REST Identity Controller
        router.GET().route( baseIdentityUrl + "user/list" ).with( RestIdentityController.class, "getUsers" );
        router.GET().route( baseIdentityUrl + "user/get" ).with( RestIdentityController.class, "getUser" );
        router.GET().route( baseIdentityUrl + "user/get-active" ).with( RestIdentityController.class, "getActiveUser" );
        router.POST().route( baseIdentityUrl + "user/add" ).with( RestIdentityController.class, "addUser" );
        router.POST().route( baseIdentityUrl + "user/auth" ).with( RestIdentityController.class, "authorizeUser" );
        router.POST().route( baseIdentityUrl + "system-owner" ).with( RestIdentityController.class, "setSystemOwner" );
        router.GET().route( baseIdentityUrl + "system-owner" ).with( RestIdentityController.class, "getSystemOwner" );
        router.POST().route( baseRelationsUrl + "owner/set" ).with( RestIdentityController.class, "setSystemOwner" );
        router.GET().route( baseRelationsUrl + "owner/get" ).with( RestIdentityController.class, "getSystemOwner" );

        //REST Repository Controller
        router.GET().route( baseRepositoryUrl + "list" ).with( RestRepositoryController.class, "list" );

        //REST Relation Controller
        router.GET().route( baseRelationsUrl + "list" ).with( RestRelationController.class, "getAllRelations" );
        /*
        router.PUT().route( baseRelationsUrl + "trust" ).with( RestRelationController.class, "addTrustRelation" );
        router.GET().route( baseRelationsUrl + "source/{fingerprint}" )
              .with( RestRelationController.class, "getRelationsByOwner" );
        router.GET().route( baseRelationsUrl + "target/{fingerprint}" )
              .with( RestRelationController.class, "getRelationsByTarget" );
        router.GET().route( baseRelationsUrl + "object/{id}" )
              .with( RestRelationController.class, "getRelationsByObject" );
        router.DELETE().route( baseRelationsUrl + "{id}" ).with( RestRelationController.class, "delete" );
        router.POST().route( baseRelationsUrl + "{id}" ).with( RestRelationController.class, "change" );
        */

        //REST Security Controller
        //router.GET().route( baseSecurityUrl + "keyman" ).with( RestIdentityController.class, "getUsers" );
    }
}
