package ai.subut.kurjun.web.conf;


import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.web.conf.routes.rest.RestRoutes;
import ai.subut.kurjun.web.controllers.*;
import com.google.inject.Inject;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */

public class Routes implements ApplicationRoutes
{
    @Inject
    private RestRoutes restRoutes;

    @Inject
    private NinjaProperties ninjaProperties;

    @Override
    public void init( final Router router )
    {
        String contextPath = ninjaProperties.getContextPath();

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
        router.POST().route( "/users/create" ).with( IdentityController.class, "createUser" );
        router.GET().route( "/users" ).with( IdentityController.class, "listUsers" );
        router.POST().route( "/logout" ).with( IdentityController.class, "logout" );
        router.POST().route( "/system/owner" ).with( IdentityController.class, "setSystemOwner");
        router.GET().route( "/system/owner" ).with( IdentityController.class, "getSystemOwner");

        // -------------------------------------------------------------------------------------------------------------
        //  Templates
        // -------------------------------------------------------------------------------------------------------------
        router.GET().route( "/" ).with( TemplateController.class, "listTemplates" );
        router.GET().route( "/templates/upload" ).with( TemplateController.class, "getUploadTemplateForm" );
        router.POST().route( "/templates/upload" ).with( TemplateController.class, "uploadTemplate" );
        router.GET().route( "/templates/{id}/info" ).with( TemplateController.class, "getTemplateInfo" );
        router.GET().route( "/templates/{id}/download" ).with( TemplateController.class, "downloadTemplate" );
        router.POST().route( "/templates/{id}/delete" ).with( TemplateController.class, "deleteTemplate" );

        // -------------------------------------------------------------------------------------------------------------
        //  Apt
        // -------------------------------------------------------------------------------------------------------------
        router.GET().route( "/apt" ).with( AptController.class, "list" );
        //router.GET().route( "/apt/{id}/info" ).with( AptController.class, "getTemplateInfo" );
        router.POST().route( "/apt/upload" ).with( AptController.class, "upload" );
        router.GET().route( "/apt/{id}/download" ).with( AptController.class, "download" );
        router.POST().route( "/apt/{id}/delete" ).with( AptController.class, "delete" );

        // -------------------------------------------------------------------------------------------------------------
        //  Raw Files
        // -------------------------------------------------------------------------------------------------------------
        router.GET().route( "/raw-files" ).with( RawFileController.class, "list" );
        //router.GET().route( "/raw-files/info" ).with( RawFileController.class, "info" );
        router.POST().route( "/raw-files/upload" ).with( RawFileController.class, "upload" );
        router.GET().route( "/raw-files/{id}/download" ).with( RawFileController.class, "download" );
        router.POST().route( "/raw-files/{id}/delete" ).with( RawFileController.class, "delete" );

        // -------------------------------------------------------------------------------------------------------------
        //  Relations
        // -------------------------------------------------------------------------------------------------------------
        router.GET().route( "/relations" ).with( RelationController.class, "getRelations" );
        router.GET().route( "/relations/trust" ).with( RelationController.class, "getAddTrustRelationForm" );
        router.POST().route( "/relations/trust" ).with( RelationController.class, "addTrustRelation" );
        router.GET().route( "/relations/{id}/change" ).with( RelationController.class, "getChangeForm" );
        //router.GET().route( "/relations/by-source" ).with( RelationController.class, "getRelationsByOwner" );
        //router.GET().route( "/relations/by-target" ).with( RelationController.class, "getRelationsByTarget" );
        //router.GET().route( "/relations/by-object" ).with( RelationController.class, "getRelationsByObject" );
        //router.POST().route( "/relations/{id}/delete" ).with( RelationController.class, "delete" );
        //router.POST().route( "/relations/{id}/change" ).with( RelationController.class, "change" );

        // -------------------------------------------------------------------------------------------------------------
        //  Repositories
        // -------------------------------------------------------------------------------------------------------------
        router.GET().route( "/repositories" ).with( RepositoryController.class, "getRepoList" );

    }
}
