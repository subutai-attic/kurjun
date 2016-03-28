package ai.subut.kurjun.web.controllers;


import ai.subut.kurjun.identity.DefaultRelationObject;
import ai.subut.kurjun.model.identity.*;
import ai.subut.kurjun.web.controllers.rest.RestIdentityController;
import ai.subut.kurjun.web.security.AuthorizedUser;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ai.subut.kurjun.web.service.RelationManagerService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

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
@Singleton
public class RelationController extends BaseController
{

    private static final Logger LOGGER = LoggerFactory.getLogger( RelationController.class );

    @Inject
    private RelationManagerService relationManagerService;

    @Inject
    private IdentityManagerService identityManagerService;

    @Inject
    private RepositoryService repositoryService;


    public Result getRelations( Context context )
    {
        //************************************
        UserSession userSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        //************************************

        if ( userSession.getUser().equals( identityManagerService.getPublicUser() ) )
        {
            return Results.html().template( "views/relations.ftl" )
                          .render( "relations", null );
        }
        else
        {
                return Results.html().template( "views/relations.ftl" )
                              .render( "relations", relationManagerService.getAllRelations() );
        }

    }


    public Result getRelationsByOwner( @AuthorizedUser UserSession userSession,
                                       @Param( "fingerprint" ) String fingerprint )
    {
        return Results.html().template( "views/_popup-view-permissions.ftl" ).render( "relations",
                relationManagerService.getTrustRelationsBySource(
                        relationManagerService.toSourceObject( identityManagerService.getUser( fingerprint ) ) ) );
    }


    public Result getRelationsByTarget( @AuthorizedUser UserSession userSession,
                                        @Param( "fingerprint" ) String fingerprint )
    {
        return Results.html().template( "views/_popup-view-permissions.ftl" ).render( "relations",
                relationManagerService
                        .getTrustRelationsByTarget( relationManagerService.toTargetObject( fingerprint ) ) );
    }


    public Result getRelationsByObject( @AuthorizedUser UserSession userSession, @Param( "id" ) String id,
                                        @Param( "name" ) String name, @Param( "version" ) String version,
                                        @Param( "md5" ) String md5, @Param( "obj_type" ) int objType )
    {
        RelationObjectType relObjType = RelationObjectType.valueOf( objType );
        relObjType = ( relObjType == null ? RelationObjectType.RepositoryContent : relObjType );
        List<Relation> rels = relationManagerService
                .getTrustRelationsByObject( relationManagerService.toTrustObject(userSession, id, null, null, null, relObjType ) );
        //.stream().filter( r -> !r.getSource().getId().equals( r.getTarget().getId() ) ).collect Collectors.toList() );
        return Results.html().template( "views/_popup-view-permissions.ftl" ).render( "relations", rels );
    }


    public Result getAddTrustRelationForm( @AuthorizedUser UserSession userSession )
    {
        List<String> repos = repositoryService.getRepositories();

        return Results.html().template( "views/_popup-add-trust-rel.ftl" ).render( "repos", repos );
    }


    public Result addTrustRelation( @AuthorizedUser UserSession userSession,
                                    @Param( "target_fprint" ) String targetFprint,
                                    @Param( "trust_obj_type" ) int trustObjType,
                                    @Param( "template_id" ) String templateId, @Param( "repo" ) String repo,
                                    @Params( "permission" ) String[] permissions, Context context,
                                    FlashScope flashScope )
    {

        if ( userSession.getUser().equals( identityManagerService.getPublicUser() ) )
        {
            return Results.notFound();
        }
        else
        {
            //UserSession userSession = (UserSession ) context.getAttribute( "USER_SESSION" );
            RelationObject owner = relationManagerService.toSourceObject( userSession.getUser() );
            RelationObject target = relationManagerService.toTargetObject( targetFprint );
            RelationObject trustObject = null;

            if ( trustObjType == RelationObjectType.RepositoryContent.getId() )
            {
                trustObject = new DefaultRelationObject();
                trustObject.setId( templateId );
                trustObject.setType( RelationObjectType.RepositoryContent.getId() );
            }
            else
            {
                trustObject = new DefaultRelationObject();
                trustObject.setId( repo );
                trustObject.setType( RelationObjectType.RepositoryTemplate.getId() );
            }
            //trustObject = relationManagerService.toTrustObject( templateId, null, null, null );
            Set<Permission> objectPermissions = new HashSet<>();
            Arrays.asList( permissions ).forEach( p -> objectPermissions.add( Permission.valueOf( p ) ) );

            Set<Permission> userPermissions = relationManagerService.checkUserPermissions( userSession, trustObject.getId(),
                    trustObject.getType() );

            if ( userPermissions.containsAll( objectPermissions ) )
            {
                Relation relation = relationManagerService.addTrustRelation( owner, target, trustObject, objectPermissions );
                if ( relation != null )
                {
                    flashScope.success( "Trust relation added." );
                }
            }
            else {
                flashScope.error( "Access denied. You don't have permissions to this object." );
            }
            
            return Results.redirect( context.getContextPath() + "/relations" );
        }
    }


    public Result delete( @PathParam( "id" ) String id, @Param( "source_id" ) String sourceId,
                          @Param( "target_id" ) String targetId, @Param( "object_id" ) String objectId, Context context,
                          FlashScope flashScope )
    {
        UserSession userSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        Relation rel;

        if ( !StringUtils.isBlank( id ) )
        {
            rel = relationManagerService.getRelation( id );
        }
        else
        {
            rel = relationManagerService.getRelation( sourceId, targetId, objectId, 0 );
        }

        if ( rel != null )
        {
            if ( relationManagerService
                    .checkUserPermissions( userSession, rel.getTrustObject().getId(), rel.getTrustObject().getType() )
                    .contains( Permission.Delete ) )
            {
                relationManagerService.removeRelation( rel );
                flashScope.success( "Deleted successfully." );
            }
            else
            {
                flashScope.error( "Access denied." );
            }
        }
        else
        {
            flashScope.error( "Relation not found." );
        }

        return Results.redirect( context.getContextPath() + "/relations" );
    }


    public Result getChangeForm( @PathParam( "id" ) String id, @Param( "source_id" ) String sourceId,
                                 @Param( "target_id" ) String targetId, @Param( "object_id" ) String objectId,
                                 Context context, FlashScope flashScope )
    {
        Relation rel;
        if ( !StringUtils.isBlank( id ) )
        {
            rel = relationManagerService.getRelation( id );
        }
        else
        {
            rel = relationManagerService.getRelation( sourceId, targetId, objectId, 0 );
        }

        return Results.html().template( "views/_popup-change-trust-rel.ftl" ).render( "relation", rel );
    }


    public Result change( @PathParam( "id" ) String id, @Params( "permission" ) String[] permissions, Context context,
                          FlashScope flashScope )
    {
        UserSession userSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        Relation rel = relationManagerService.getRelation( id );

        if ( rel != null )
        {
            if ( relationManagerService
                    .checkUserPermissions( userSession, rel.getTrustObject().getId(), rel.getTrustObject().getType() )
                    .contains( Permission.Update ) )

            {
                Set<Permission> objectPermissions = new HashSet<>();
                Arrays.asList( permissions ).forEach( p -> objectPermissions.add( Permission.valueOf( p ) ) );
                rel.setPermissions( objectPermissions );
                relationManagerService.saveRelation( rel );
                flashScope.success( "Saved successfully." );
            }
            else
            {
                flashScope.error( "Access denied." );
            }
        }
        else
        {
            flashScope.error( "Relation not found." );
        }

        return Results.redirect( context.getContextPath() + "/relations" );
    }
}
