package ai.subut.kurjun.web.service.impl;


import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.identity.service.RelationManager;
import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.web.controllers.rest.RestIdentityController;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ai.subut.kurjun.web.service.RelationManagerService;
import ai.subut.kurjun.common.ErrorCode;


/**
 * Service for managing trust relations
 */
@Singleton
public class RelationManagerServiceImpl implements RelationManagerService
{

    private static final Logger LOGGER = LoggerFactory.getLogger( RestIdentityController.class );

    @Inject
    private RelationManager relationManager;

    @Inject
    private IdentityManagerService identityManagerService;


    //*************************************
    @Override
    public List<Relation> getAllRelations( UserSession uSession )
    {
        if ( !uSession.getUser().equals( identityManagerService.getPublicUser() ) )
        {
            return relationManager.getAllRelations();
        }
        else
        {
            return Collections.emptyList();
        }
    }


    //*************************************
    @Override
    public Relation getRelation( UserSession uSession , long relationId )
    {
        if ( !uSession.getUser().equals( identityManagerService.getPublicUser() ) )
        {
            return relationManager.getRelation( relationId );
        }
        else
        {
            return null;
        }
    }


    //*************************************
    @Override
    public void removeRelation( UserSession uSession, Relation relation )
    {
        User user = uSession.getUser();

        if ( user != null && relation != null )
        {
            RelationObject object = relation.getTrustObject();
            Set<Permission> permissoins = relationManager.getUserPermissions( user, object.getObjectId(), object.getType() );
            if ( permissoins.contains( Permission.Delete ) && !user.equals( identityManagerService.getPublicUser() ))
            {
                LOGGER.info( "-------------------- deleting -----------------" );
                relationManager.removeRelation( relation.getId() );
            }
            else
            {
                throw new IllegalAccessError( "Access denied" );
            }
        }
    }


    //*************************************
    @Override
    public int addTrustRelation( UserSession uSession, String targeObjId, int targetObjType, String trustObjId,
                                 int trustObjType, Set<Permission> permissions )
    {
        if ( !uSession.getUser().equals( identityManagerService.getPublicUser() ) )
        {
            RelationObject targetObj = relationManager.createRelationObject( targeObjId ,targetObjType  ) ;
            RelationObject trustObj  = relationManager.createRelationObject( trustObjId ,trustObjType  ) ;


            Relation rel = relationManager.buildTrustRelation( uSession.getUser(), targetObj, trustObj, permissions );

            if(rel == null)
            {
                return ErrorCode.SystemError.getId();
            }
            else
            {
                return ErrorCode.Success.getId();
            }
        }
        else
        {
            return 2; //Permission Denied
        }
    }

    public List<Relation> getRelationsByObject( String trustObjectId, int trustObjectType )
    {
        return  relationManager.getRelationsByObject( trustObjectId, trustObjectType );
    }


    @Override
    public void changePermissions( UserSession userSession, long relationId, String[] permissions )
    {
        User owner = userSession.getUser();
        Relation relation = relationManager.getRelation( relationId );

        if (relation != null && owner != null)
        {
            if (relation.getSource().getObjectId().equalsIgnoreCase( owner.getKeyFingerprint() ))
            {
                Set<Permission> permissionSet = new HashSet<>();
                for ( String s : permissions )
                {
                    permissionSet.add( Permission.valueOf( s ) );
                }

//                LOGGER.info( "------------ user {} sets following permissions to object {}  -------------------",
//                        owner.getKeyFingerprint(), relation.getTrustObject().getObjectId() );
//                permissionSet.forEach( p -> LOGGER.info( p.getName() ) );

                relation.setPermissions( permissionSet );
                relationManager.saveRelation( relation );
            }
            else throw new IllegalAccessError( "You are not owner of this permission" );
        }
        else throw new EntityNotFoundException( "Relation not found" );
    }
}
