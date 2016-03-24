package ai.subut.kurjun.web.service.impl;


import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
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
import ai.subut.kurjun.web.controllers.rest.RestIdentityController;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ai.subut.kurjun.web.service.RelationManagerService;
import ai.subut.kurjun.web.service.RepositoryService;
import ai.subut.kurjun.web.service.TemplateManagerService;


/**
 * Service for managing trust relations
 */
public class RelationManagerServiceImpl implements RelationManagerService
{

    private static final Logger LOGGER = LoggerFactory.getLogger( RestIdentityController.class );


    private UserSession userSession;

    @Inject
    private RelationManager relationManager;

    @Inject
    private IdentityManagerService identityManagerService;

    @Inject
    private TemplateManagerService templateManagerService;

    @Inject
    private RepositoryService repositoryService;



    //*************************************
    @Override
    public List<Relation> getAllRelations()
    {
        return relationManager.getAllRelations();
    }


    //*************************************
    @Override
    public void removeRelation( Relation relation )
    {
        relationManager.removeRelation( relation.getId() );
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
        return relationManager.getRelationsBySource( sourceObject );
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
    public Relation getRelation( String relationId )
    {
        return relationManager.getRelation( relationId );
    }


    //*************************************
    @Override
    public List<Relation> getRelationsByObject( String trustObjectId, int trustObjectType )
    {
        return relationManager.getRelationsByObject( trustObjectId, trustObjectType );
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
            if ( objectId.equals( "vapt" ))
            {
                owner = identityManagerService.getSystemOwner();
                pubus = identityManagerService.getPublicUser();

                buildTrustRelation( owner,pubus , objectId, objectType, buildPermissions( Permission.Read.getId() ) );
            }
            else if ( objectId.equals( "public" ) || objectId.equals( "raw" ))
            {
                owner = identityManagerService.getSystemOwner();
                pubus = identityManagerService.getPublicUser();

                buildTrustRelation( owner,pubus , objectId, objectType, buildPermissions( Permission.Write.getId() ) );
            }
            else
            {
                owner = userSession.getUser();
            }

            buildTrustRelation( owner, owner, objectId, objectType, buildPermissions( Permission.Delete.getId() ) );
        }
    }


    //***************************
    @Override
    public Set<Permission> checkUserPermissions( UserSession userSession, String objectId, int objectType )
    {
        if(userSession == null)
            return null;
        else
            return relationManager.getUserPermissions( userSession.getUser() ,objectId ,objectType );
    }


    //*******************************************************************
    @Override
    public boolean checkRepoPermissions( UserSession userSession, String repoId, int repoType, String contentId,
                                         int contentType, Permission perm )
    {
        boolean access = false;

        if ( checkUserPermissions( userSession, repoId, repoType ).contains( perm ) )
        {
            access = true;
        }

        if ( !Strings.isNullOrEmpty( contentId ) )
        {

            if ( access == false )
            {
                if ( checkUserPermissions( userSession, contentId, contentType ).contains( perm ) )
                {
                    access = true;
                }
            }
        }
        return access;
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
    public RelationObject toTrustObject( String id, String md5, String name, String version, RelationObjectType relObjType )
    {
        TemplateId tid;
        DefaultTemplate defaultTemplate;
        RelationObject trustObject = null;

        if ( RelationObjectType.RepositoryContent == relObjType ) {
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
        } else if ( RelationObjectType.RepositoryTemplate == relObjType ) {
            List<String> repos = repositoryService.getRepositories();
            if ( repos.contains( id ) ) {
                trustObject = new DefaultRelationObject();
                trustObject.setId( id );
                trustObject.setType( RelationObjectType.RepositoryTemplate.getId() );
            }
        }


        return trustObject;
    }

    @Override
    public void saveRelation( Relation relation )
    {
        relationManager.saveTrustRelation( relation );
    }

}
