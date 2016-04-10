package ai.subut.kurjun.web.controllers.rest;

import java.util.List;

import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.web.controllers.BaseController;
import ai.subut.kurjun.web.security.AuthorizedUser;
import ninja.Context;
import ninja.Result;
import ninja.Results;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ai.subut.kurjun.model.identity.*;
import ai.subut.kurjun.web.service.RelationManagerService;
import ninja.params.Param;
import ninja.params.Params;
import ninja.params.PathParam;
import ninja.session.FlashScope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityNotFoundException;


/**
 * REST Controller for Trust Relation Management
 */
@Singleton
public class RestRelationController extends BaseController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( RestIdentityController.class );


    @Inject
    private RelationManagerService relationManagerService;



    //*************************************************
    public Result getAllRelations(Context context)
    {
        //************************************
        UserSession userSession = (UserSession ) context.getAttribute( "USER_SESSION" );
        //************************************

        List<Relation> relations = relationManagerService.getAllRelations(userSession);
        return Results.ok().render( relations ).json();

    }


    //*************************************************
    public Result addTrustRelation( @Param( "target_obj_id" ) String sourceObjId,
                                    @Param( "target_obj_type" ) int sourceObjType,
                                    @Param( "trust_obj_id" ) String trustObjId,
                                    @Param( "trust_obj_type" ) int trustObjType,
                                    @Params( "permission" ) String[] permissions, Context context)
    {
        //************************************
        UserSession userSession = (UserSession ) context.getAttribute( "USER_SESSION" );
        //************************************

        Set<Permission> objectPermissions = new HashSet<>();
        Arrays.asList( permissions ).forEach( p -> objectPermissions.add( Permission.valueOf( p ) ) );

        int result = relationManagerService
                .addTrustRelation( userSession, sourceObjId, sourceObjType, trustObjId, trustObjType,
                        objectPermissions );


        if ( result == 0 )
        {
            return Results.ok();
        }
        else if ( result == 2 )
        {
            return Results.forbidden();
        }
        else
        {
            return Results.internalServerError();
        }
    }


    public Result getRelationsByObject( Context context, @Param( "id" ) String id,
                                        @Param( "name" ) String name, @Param( "version" ) String version,
                                        @Param( "md5" ) String md5, @Param( "obj_type" ) int objType )
    {
        List<Relation> rels = relationManagerService.getRelationsByObject( id, objType );

        return Results.ok().json().render( rels );
    }

    public Result delete( @PathParam( "id" ) String id, Context context )
    {
        try
        {
            //************************************
            UserSession userSession = (UserSession ) context.getAttribute( "USER_SESSION" );
            //************************************

            long relationID = Long.parseLong( id );
            int result = relationManagerService.removeRelation( userSession, relationID );

            if ( result == 0 )
            {
                return Results.ok();
            }
            else if ( result == 2 )
            {
                return Results.forbidden();
            }
            else
            {
                return Results.internalServerError();
            }
        }
        catch ( IllegalAccessError e )
        {
            return Results.internalServerError();
        }
    }


    public Result change( @AuthorizedUser UserSession userSession, @PathParam( "id" ) String id,
                          @Params( "permission" ) String[] permissions )
    {
        try
        {
            relationManagerService.changePermissions( userSession, Long.valueOf( id ), permissions );
            return Results.ok().json().render( "Updated successfully" );
        }
        catch ( EntityNotFoundException e )
        {
            return Results.notFound().json().render( "Trust relation not found" );
        }
        catch ( IllegalAccessError e )
        {
            return Results.forbidden().json().render( "You don't have access to edit permissions for this trust relation" );
        }
    }
}
