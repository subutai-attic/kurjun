package ai.subut.kurjun.web.controllers;


import ai.subut.kurjun.model.identity.*;
import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.web.security.AuthorizedUser;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityNotFoundException;

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
    private RepositoryService repositoryService;


    //*************form *********************
    public Result getChangeForm( @AuthorizedUser UserSession userSession, @PathParam( "id" ) String id,
                                 @Param( "source_id" ) String sourceId, @Param( "target_id" ) String targetId,
                                 @Param( "object_id" ) String objectId, Context context, FlashScope flashScope )
    {
        Relation rel = null;
        if ( !StringUtils.isBlank( id ) )
        {
            rel = relationManagerService.getRelation( userSession, Long.parseLong( id ) );
        }
        else
        {
            //rel = relationManagerService.getRelation( sourceId, targetId, objectId, 0 );
        }

        return Results.html().template( "views/_popup-change-permissions.ftl" ).render( "relation", rel );
    }


    //*************form *********************
    public Result getAddTrustRelationForm( @AuthorizedUser UserSession userSession )
    {
        List<RepositoryData> repos = repositoryService.getRepositoryList(ObjectType.All.getId());

        return Results.html().template( "views/_popup-add-trust-rel.ftl" ).render( "repos", repos );
    }


    //*************************************************
    public Result getRelations( Context context )
    {
        //****************************
        UserSession userSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        //****************************
        List<Relation> rels = relationManagerService.getAllRelations( userSession );

        if ( rels.isEmpty() )
        {
            return Results.html().template( "views/permissions.ftl" ).render( "relations", null );
        }
        else
        {
            Map<String, String> map = ObjectType.getMap();

            return Results.html().template( "views/permissions.ftl" )
                          .render( "relations", rels )
                          .render( "relObjTypes", map );
        }
    }


    //*************************************************
    public Result addTrustRelation( @AuthorizedUser UserSession userSession,
                                    @Param( "target_obj_id" ) String targetObjId,
                                    @Param( "target_obj_type" ) int targetObjType,
                                    @Param( "trust_obj_id" ) String trustObjId,
                                    @Param( "trust_obj_type" ) int trustObjType,
                                    @Params( "permission" ) String[] permissions, Context context,
                                    FlashScope flashScope )
    {

        Set<Permission> objectPermissions = new HashSet<>();
        Arrays.asList( permissions ).forEach( p -> objectPermissions.add( Permission.valueOf( p ) ) );

        int result = relationManagerService
                .addTrustRelation( userSession, targetObjId, targetObjType, trustObjId, trustObjType,
                        objectPermissions );

        if ( result == 0 )
        {
            flashScope.success( "Trust relation added." );
        }
        else if ( result == 1 )
        {
            flashScope.error( "Internal System error." );
        }
        else if ( result == 2 )
        {
            flashScope.error( "Access denied. You don't have permissions to this object." );
        }


        return Results.redirect( context.getContextPath() + "/permissions" );
    }



    public Result getRelationsByObject( @AuthorizedUser UserSession userSession, @Param( "id" ) String id,
                                        @Param( "name" ) String name, @Param( "version" ) String version,
                                        @Param( "md5" ) String md5, @Param( "obj_type" ) int objType )
    {
        List<Relation> rels = relationManagerService.getRelationsByObject( id, objType );

        return Results.html().template( "views/_popup-view-permissions.ftl" ).render( "relations", rels );
    }



    public Result delete( @AuthorizedUser UserSession userSession, @PathParam( "id" ) String id, Context context,
                          FlashScope flashScope )
    {
        try
        {
            long relationID = Long.parseLong( id );
            int result = relationManagerService.removeRelation( userSession, relationID );

            if ( result == 0 )
            {
                flashScope.success( "Deleted successfully" );
            }
            else if ( result == 1 )
            {
                flashScope.error( "Internal System error." );
            }
            else if ( result == 2 )
            {
                flashScope.error( "Access denied. You don't have permissions to this object." );
            }
        }
        catch ( IllegalAccessError e )
        {
            flashScope.error( "Access denied" );
        }

        return Results.redirect( context.getContextPath() + "/permissions" );
    }


    public Result change( @AuthorizedUser UserSession userSession, @PathParam( "id" ) String id,
                          @Params( "permission" ) String[] permissions, Context context, FlashScope flashScope )
    {
        //UserSession userSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        try
        {
            relationManagerService.changePermissions( userSession, Long.valueOf( id ), permissions );
            flashScope.success( "Permissions changed." );
        }
        catch ( EntityNotFoundException e )
        {
            flashScope.error( "Permission not found" );
        }
        catch ( IllegalAccessError e )
        {
            flashScope.error( "You don't have access to edit permissions for this trust relation" );
        }

        return Results.redirect( context.getContextPath() + "/permissions" );
    }
}
