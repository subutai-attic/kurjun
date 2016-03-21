package ai.subut.kurjun.web.service.impl;


import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;

import ai.subut.kurjun.identity.DefaultRelationObject;
import ai.subut.kurjun.identity.service.RelationManager;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.metadata.common.subutai.TemplateId;
import ai.subut.kurjun.metadata.common.utils.IdValidators;
import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.RelationObjectType;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ai.subut.kurjun.web.service.RelationManagerService;
import ai.subut.kurjun.web.service.TemplateManagerService;


/**
 * Service for managing trust relations
 */
public class RelationManagerServiceImpl implements RelationManagerService
{

    private UserSession userSession;

    @Inject
    private RelationManager relationManager;

    @Inject
    private IdentityManagerService identityManagerService;

    @Inject
    private TemplateManagerService templateManagerService;



    //*************************************
    @Override
    public List<Relation> getAllRelations()
    {
        return relationManager.getAllRelations();
    }


    //*************************************
    @Override
    public Relation addTrustRelation( RelationObject source, RelationObject target, RelationObject trustObject,
                                      Set<Permission> permissions )
    {
        return relationManager.buildTrustRelation( source, target, trustObject, permissions );
    }


    //*************************************
    @Override
    public List<Relation> getTrustRelationsBySource( RelationObject sourceObject )
    {
        if ( userSession != null && userSession.getUser() != null )
        {
            return relationManager.getRelationsBySource( toSourceObject( userSession.getUser() ) );
        }

        return Collections.emptyList();
    }


    //*************************************
    @Override
    public List<Relation> getTrustRelationsByTarget( RelationObject targetObject )
    {
        return relationManager.getRelationsByTarget( targetObject );
    }


    //*************************************
    @Override
    public List<Relation> getTrustRelationsByObject( RelationObject trustObject )
    {
        return relationManager.getRelationsByObject( trustObject );
    }


    //*************************************
    @Override
    public void setUserSession( UserSession userSession )
    {
        this.userSession = userSession;
    }


    //*************************************
    @Override
    public Relation getRelation( String sourceId, String targetId , String trustObjectId , int trustObjectType)
    {
        return relationManager.getRelation( sourceId, targetId, trustObjectId, trustObjectType );
    }


    //*************************************
    @Override
    public List<Relation> getRelationsByObject( String trustObjectId, int trustObjectType )
    {
        return getRelationsByObject( trustObjectId, trustObjectType );
    }


    //***************************
    @Override
    public Relation getObjectOwner( String trustObjectId, int trustObjectType )
    {
        return relationManager.getObjectOwner( trustObjectId, trustObjectType );
    }



    //***************************
    @Override
    public Relation buildTrustRelation( User sourceUser, User targetUser, String trustObjectId, int trustObjectType,
                                        Set<Permission> permissions )
    {
        return relationManager.buildTrustRelation( sourceUser, targetUser, trustObjectId,trustObjectType, permissions );
    }


    //***************************
    @Override
    public Set<Permission> buildPermissions( int permLevel )
    {
        return relationManager.buildPermissions( permLevel );
    }


    //***************************
    @Override
    public void checkRelationOwner( UserSession userSession, String objectId, int objectType )
    {
        Relation relation = null;
        User owner = null;
        User pubus = null;

        relation = getObjectOwner( objectId, objectType );

        if ( relation == null )
        {
            if ( objectId.equals( "public" ) )
            {
                owner = identityManagerService.getSystemOwner();
                pubus = identityManagerService.getPublicUser();

                buildTrustRelation( owner,pubus , objectId, objectType, buildPermissions( 2 ) );
            }
            else
            {
                owner = userSession.getUser();
            }

            buildTrustRelation( owner, owner, objectId, objectType, buildPermissions( 4 ) );
        }
    }


    //***************************
    @Override
    public Set<Permission> checkUserPermissions( UserSession userSession, String objectId, int objectType )
    {
        return relationManager.getUserPermissions( userSession.getUser() ,objectId ,objectType );
    }


    //*******************************************************************************
    @Override
    public RelationObject toSourceObject( User user )
    {
        if ( user != null )
        {
            RelationObject owner = new DefaultRelationObject();
            owner.setId( user.getKeyFingerprint() );
            owner.setType( RelationObjectType.User.getId() );

            return owner;
        }
        else
        {
            return null;
        }
    }


    @Override
    public RelationObject toTargetObject( String fingerprint )
    {
        User targetUser = identityManagerService.getUser( fingerprint );
        RelationObject targetObject = null;

        if ( targetUser != null )
        {
            targetObject = new DefaultRelationObject();
            targetObject.setId( targetUser.getKeyFingerprint() );
            targetObject.setType( RelationObjectType.User.getId() );
        }

        return targetObject;
    }


    @Override
    public RelationObject toTrustObject( String id, String md5, String name, String version )
    {
        TemplateId tid;
        DefaultTemplate defaultTemplate;
        RelationObject trustObject = null;

        if ( id != null )
        {
            tid = IdValidators.Template.validate( id );
            defaultTemplate = templateManagerService.getTemplate( tid, md5, name, version );
        }
        else
        {
            defaultTemplate = templateManagerService.getTemplate( null, md5, name, version );
        }
        if ( defaultTemplate != null )
        {
            trustObject = new DefaultRelationObject();
            trustObject.setId( defaultTemplate.getId().toString() );
            trustObject.setType( RelationObjectType.RepositoryContent.getId() );
        }

        return trustObject;
    }



}