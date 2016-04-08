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

    /*
    public Result getRelationsByOwner( @PathParam( "fingerprint" ) String fingerprint )
    {
        return Results.ok().json().render( relationManagerService.getTrustRelationsBySource(
                relationManagerService.toSourceObject( identityManagerService.getUser( fingerprint ) ) ) );
    }


    public Result getRelationsByTarget( @PathParam( "fingerprint" ) String fingerprint )
    {
        return Results.ok().json().render( relationManagerService.getTrustRelationsByTarget(
                relationManagerService.toTargetObject(fingerprint) ) );
    }
    */

    public Result getRelationsByObject( Context context, @Param( "id" ) String id,
                                        @Param( "name" ) String name, @Param( "version" ) String version,
                                        @Param( "md5" ) String md5, @Param( "obj_type" ) int objType )
    {
        List<Relation> rels = relationManagerService.getRelationsByObject( id, objType );

        return Results.ok().json().render( rels );
    }

    /*
    public Result addTrustRelation( @AuthorizedUser UserSession userSession, @Param( "fingerprint" ) String fingerprint,
                                    @Param( "id" ) String id, @Params( "permission" ) String[] permissions,
                                    @Param("trust_obj_type") int trustObjType )
    {
        if ( userSession.getUser().equals( identityManagerService.getPublicUser() ) )
        {
            return Results.notFound();
        }
        else
        {
            RelationObject owner = relationManagerService.toSourceObject( userSession.getUser() );
            RelationObject target = relationManagerService.toTargetObject( fingerprint );

            RelationObjectType relObjType = RelationObjectType.RepositoryContent;
            if ( trustObjType == RelationObjectType.RepositoryTemplate.getId() ) // repository
            {
                relObjType = RelationObjectType.RepositoryTemplate;
            }
            RelationObject trustObject;// = relationManagerService.toTrustObject(userSession, id, null, null, null, relObjType );
            trustObject = new DefaultRelationObject();
            trustObject.setId( id );
            trustObject.setType( relObjType.getId() );

            Set<Permission> objectPermissions = new HashSet<>();
            Arrays.asList( permissions ).forEach( p -> objectPermissions.add( Permission.valueOf( p ) ) );

            Set<Permission> userPermissions = relationManagerService.checkUserPermissions( userSession, trustObject.getId(),
                    trustObject.getType() );

            if ( userPermissions.containsAll( objectPermissions ) )
            {
                Relation relation =
                        relationManagerService.addTrustRelation( owner, target, trustObject, objectPermissions );
                if ( relation != null )
                {
                    return Results.ok().json().render("");
                }
                else
                {
                    return Results.notFound().json().render( "Failed to save trust relation. Object not found." );
                }
            }
            else {
                return Results.forbidden().json().render( "You don't have access to this object" );
            }
        }
    }
    */

    public Result delete( @AuthorizedUser UserSession userSession, @PathParam("id") String id )
    {
        Relation relation  = relationManagerService.getRelation( userSession, Long.valueOf( id ) );
        try
        {
            relationManagerService.removeRelation( userSession, relation );
            return Results.ok().json().render( "Deleted successfully" );
        }
        catch ( IllegalAccessError e )
        {
            return Results.forbidden().json().render( "Access denied" );
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
