package ai.subut.kurjun.web.service.impl;


import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.identity.service.RelationManager;
import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.RelationObject;
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
        if ( !uSession.getUser().equals( identityManagerService.getPublicUser() ) )
        {
            relationManager.removeRelation( relation.getId() );
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

}
