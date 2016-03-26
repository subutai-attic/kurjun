package ai.subut.kurjun.web.controllers;

import ai.subut.kurjun.identity.DefaultRelationObject;
import ai.subut.kurjun.model.identity.*;
import ai.subut.kurjun.web.controllers.rest.RestIdentityController;
import ai.subut.kurjun.web.security.AuthorizedUser;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ai.subut.kurjun.web.service.RelationManagerService;
import com.google.inject.Inject;

import ai.subut.kurjun.web.service.RepositoryService;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;


/**
 * Web Controller for Trust Relation Management
 */
public class RelationController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger( RelationController.class );

    @Inject
    private RelationManagerService relationManagerService;

    @Inject
    private IdentityManagerService identityManagerService;

    @Inject
    private RepositoryService repositoryService;

    public Result getRelations( /*@AuthorizedUser UserSession userSession,*/ Context context, Session session )
    {
        UserSession userSession = (UserSession ) context.getAttribute( "USER_SESSION" );
        return Results.html().template("views/relations.ftl").render( "relations",
                relationManagerService.getAllRelations());
    }


    public Result getRelationsByOwner( /*@AuthorizedUser UserSession userSession,*/ @Param( "fingerprint" ) String fingerprint )
    {
        return Results.html().template("views/_popup-view-permissions.ftl").render( "relations",
                relationManagerService.getTrustRelationsBySource(
                        relationManagerService.toSourceObject( identityManagerService.getUser( fingerprint ) ) ));
    }


    public Result getRelationsByTarget( /*@AuthorizedUser UserSession userSession,*/ @Param( "fingerprint" ) String fingerprint )
    {
        return Results.html().template("views/_popup-view-permissions.ftl").render( "relations",
                relationManagerService.getTrustRelationsByTarget(
                        relationManagerService.toTargetObject(fingerprint) ) );
    }


    public Result getRelationsByObject( /*@AuthorizedUser UserSession userSession,*/ @Param( "id" ) String id,
                                        @Param( "name" ) String name, @Param( "version" ) String version,
                                        @Param( "md5" ) String md5, @Param( "obj_type" ) int objType )
    {
        RelationObjectType relObjType = RelationObjectType.valueOf( objType );
        relObjType = ( relObjType == null? RelationObjectType.RepositoryContent : relObjType );
        List<Relation> rels = relationManagerService.getTrustRelationsByObject(
                relationManagerService.toTrustObject( id, null, null, null, relObjType ) );
        //.stream().filter( r -> !r.getSource().getId().equals( r.getTarget().getId() ) ).collect Collectors.toList() );
        return Results.html().template("views/_popup-view-permissions.ftl").render( "relations", rels );
    }


    public Result getAddTrustRelationForm()
    {
        List<String> repos = repositoryService.getRepositories();
        repos.remove( "vapt" );
        repos.remove( "raw" );

        return Results.html().template("views/_popup-add-trust-rel.ftl").render("repos", repos);
    }


    public Result addTrustRelation( @Param( "target_fprint" ) String targetFprint,
                                    @Param("trust_obj_type") int trustObjType,
                                    @Param( "template_id" ) String templateId, @Param("repo") String repo,
                                    @Params( "permission" ) String[] permissions,
                                    Context context, FlashScope flashScope )
    {
        UserSession userSession = (UserSession ) context.getAttribute( "USER_SESSION" );
        RelationObject owner = relationManagerService.toSourceObject( userSession.getUser() );
        RelationObject target = relationManagerService.toTargetObject( targetFprint );
        RelationObject trustObject = null;

        if ( trustObjType == RelationObjectType.RepositoryContent.getId() )
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

        return Results.redirect(context.getContextPath()+"/relations");
    }


    public Result delete( @PathParam("id") String id, @Param("source_id") String sourceId,
                          @Param("target_id") String targetId, @Param("object_id") String objectId,
                          Context context, FlashScope flashScope )
    {
        //*****************************************************
        UserSession userSession = (UserSession ) context.getAttribute( "USER_SESSION" );
        relationManagerService.setUserSession( userSession );
        //*****************************************************

        boolean deleted = false;

        if ( !StringUtils.isBlank(id) )
        {
            Relation rel = relationManagerService.getRelation( id );
            if ( rel != null )
            {
                if(rel.getTarget().getId().equals( userSession.getUser().getKeyFingerprint() ))
                {
                    relationManagerService.removeRelation( rel );
                    deleted = true;
                }
            }
        }
        else
        {
            relationManagerService.getRelation( sourceId, targetId, objectId, 0 );
            deleted = true;
        }

        if ( deleted )
            flashScope.success( "Deleted successfully." );
        else
            flashScope.error( "Failed to delete." );

        return Results.redirect( context.getContextPath()+"/relations" );
    }


    public Result getChangeForm( @PathParam("id") String id, @Param("source_id") String sourceId,
                                 @Param("target_id") String targetId, @Param("object_id") String objectId,
                                 Context context, FlashScope flashScope )
    {
        Relation rel;
        if ( !StringUtils.isBlank(id) ) {
            rel = relationManagerService.getRelation( id );
        }
        else {
            rel = relationManagerService.getRelation( sourceId, targetId, objectId, 0 );
        }

        return Results.html().template( "views/_popup-change-trust-rel.ftl" ).render( "relation", rel );
    }


    public Result change( @PathParam( "id" ) String id, @Params( "permission" ) String[] permissions,
                                    Context context, FlashScope flashScope )
    {
        Relation rel = relationManagerService.getRelation( id );

        if ( rel != null )
        {
            Set<Permission> objectPermissions = new HashSet<>();
            Arrays.asList( permissions ).forEach( p -> objectPermissions.add(Permission.valueOf(p)) );
            rel.setPermissions( objectPermissions );
            relationManagerService.saveRelation( rel );
            flashScope.success( "Saved successfully." );
        }

        return Results.redirect(context.getContextPath()+"/relations");
    }
}
