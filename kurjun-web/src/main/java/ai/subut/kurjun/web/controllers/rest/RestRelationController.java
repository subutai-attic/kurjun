package ai.subut.kurjun.web.controllers.rest;

import java.util.List;

import ai.subut.kurjun.identity.DefaultRelationObject;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.web.controllers.BaseController;
import ai.subut.kurjun.web.service.IdentityManagerService;
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

import org.apache.commons.lang.StringUtils;


/**
 * REST Controller for Trust Relation Management
 */
@Singleton
public class RestRelationController extends BaseController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( RestIdentityController.class );

    @Inject
    IdentityManagerService identityManagerservice;

    @Inject
    private RelationManagerService relationManagerService;

    @Inject
    private IdentityManagerService identityManagerService;


    public Result getAllRelations(Context context)
    {
        //************************************
        UserSession userSession = (UserSession ) context.getAttribute( "USER_SESSION" );
        //************************************

        if ( userSession.getUser().equals( identityManagerService.getPublicUser() ) )
        {
            return null;
        }
        else
        {
            List<Relation> relations = relationManagerService.getAllRelations();
            return Results.ok().render( relations ).json();
        }
    }


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


    public Result getRelationsByObject( Context context, @PathParam( "id" ) String id,
                                        @Param( "name" ) String name, @Param( "version" ) String version,
                                        @Param( "md5" ) String md5, @Param( "rel_obj_type" ) int relObjType )
    {
        UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );


        RelationObjectType objType = RelationObjectType.valueOf( relObjType );
        if ( objType == null ) {
            objType = RelationObjectType.RepositoryContent;
        }
        return Results.ok().json().render( relationManagerService.getTrustRelationsByObject(
                relationManagerService.toTrustObject(uSession, id, name, md5, version, objType)));
    }


    public Result addTrustRelation( Context context, @Param( "fingerprint" ) String fingerprint,
                                    @Param( "id" ) String id, @Params( "permission" ) String[] permissions,
                                    @Param("trust_obj_type") int trustObjType )
    {


        //*****************************************************
        UserSession userSession = (UserSession ) context.getAttribute( "USER_SESSION" );
        //*****************************************************

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
            RelationObject trustObject = relationManagerService.toTrustObject(userSession, id, null, null, null, relObjType );

            Set<Permission> objectPermissions = new HashSet<>();
            Arrays.asList( permissions ).forEach( p -> objectPermissions.add( Permission.valueOf( p ) ) );

            Relation relation = relationManagerService.addTrustRelation( owner, target, trustObject, objectPermissions );
            if ( relation != null )
            {
                return Results.ok();
            }
            else
            {
                return Results.notFound();
            }
        }
    }

    public Result delete( Context context, @PathParam("id") String id, @Param("source_id") String sourceId,
                          @Param("target_id") String targetId, @Param("object_id") String objectId )
    {

        //*****************************************************
        UserSession userSession = (UserSession ) context.getAttribute( "USER_SESSION" );
        //*****************************************************

        boolean deleted = false;

        if ( !StringUtils.isBlank(id ) )
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
            return Results.ok();
        else
            return Results.badRequest().text().render( "Failed to delete." );


    }

    public Result change( @PathParam( "id" ) String id, @Params( "permission" ) String[] permissions )
    {
        Relation rel = relationManagerService.getRelation( id );
        if ( rel != null ) {
            Set<Permission> objectPermissions = new HashSet<>();
            Arrays.asList( permissions ).forEach( p -> objectPermissions.add(Permission.valueOf(p)) );
            rel.setPermissions( objectPermissions );
            relationManagerService.saveRelation( rel );
            return Results.ok();
        }

        return Results.notFound();
    }
}
