package ai.subut.kurjun.web.controllers;

import ai.subut.kurjun.identity.DefaultRelationObject;
import ai.subut.kurjun.model.identity.*;
import ai.subut.kurjun.web.controllers.rest.RestIdentityController;
import ai.subut.kurjun.web.security.AuthorizedUser;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ai.subut.kurjun.web.service.RelationManagerService;
import com.google.inject.Inject;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.Params;
import ninja.params.PathParam;
import ninja.session.FlashScope;
import ninja.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Web Controller for Trust Relation Management
 */
public class RelationController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger( RestIdentityController.class );

    @Inject
    private RelationManagerService relationManagerService;

    @Inject
    private IdentityManagerService identityManagerService;


    public Result getRelations( /*@AuthorizedUser UserSession userSession,*/ Context context, Session session )
    {
        UserSession userSession = (UserSession ) context.getAttribute( "USER_SESSION" );
        return Results.html().template("views/relations.ftl").render( "relations",
                relationManagerService.getAllRelations());
    }

    public Result getRelationsByOwner( /*@AuthorizedUser UserSession userSession,*/ @Param( "fingerprint" ) String fingerprint )
    {

        return Results.html().template("views/relations.ftl").render( "relations",
                relationManagerService.getTrustRelationsBySource(
                        relationManagerService.toSourceObject( identityManagerService.getUser( fingerprint ) )
                ) );
    }

    public Result getRelationsByTarget( /*@AuthorizedUser UserSession userSession,*/ @Param( "fingerprint" ) String fingerprint )
    {
        return Results.html().template("views/relations.ftl").render( "relations",
                relationManagerService.getTrustRelationsByTarget(
                        relationManagerService.toTargetObject(fingerprint) ) );
    }

    public Result getRelationsByObject( /*@AuthorizedUser UserSession userSession,*/ @PathParam( "id" ) String id,
                                        @Param( "name" ) String name, @Param( "version" ) String version,
                                        @Param( "md5" ) String md5 )
    {
        return Results.html().template("views/relations.ftl").render( "relations",
                relationManagerService.getTrustRelationsByTarget(
                        relationManagerService.toTrustObject(id, name, md5, version)));
    }

    public Result addTrustRelation( /*@AuthorizedUser UserSession userSession,*/ @Param( "target_fprint" ) String targetFprint,
                                   @Param( "template_id" ) String templateId, @Param("repo") String repo,
                                    @Param("trust_obj_type") String trustObjType,
                                    @Params( "permission" ) String[] permissions,
                                   Context context, FlashScope flashScope )
    {
        UserSession userSession = (UserSession ) context.getAttribute( "USER_SESSION" );
        RelationObject owner = relationManagerService.toSourceObject( userSession.getUser() );
        RelationObject target = relationManagerService.toTargetObject( targetFprint );
        RelationObject trustObject = null;

        if ( trustObjType.equals("template"))
        {
            trustObject = new DefaultRelationObject();
            trustObject.setId(templateId);
            trustObject.setType(RelationObjectType.RepositoryContent.getId());
        }
        else
        {
            trustObject = new DefaultRelationObject();
            trustObject.setId(repo);
            trustObject.setType(RelationObjectType.RepositoryTemplate.getId());
        }
        //trustObject = relationManagerService.toTrustObject( templateId, null, null, null );
        Set<Permission> objectPermissions = new HashSet<>();
        Arrays.asList( permissions ).forEach( p -> objectPermissions.add(Permission.valueOf(p)) );

        Relation relation = relationManagerService.addTrustRelation(owner, target, trustObject, objectPermissions);
        if ( relation != null )
        {
            flashScope.success("Trust relation added.");
        }

        return Results.redirect("/relations");
    }
}
