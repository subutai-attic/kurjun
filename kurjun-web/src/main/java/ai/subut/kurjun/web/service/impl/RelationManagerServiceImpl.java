package ai.subut.kurjun.web.service.impl;


import ai.subut.kurjun.identity.DefaultRelationObject;
import ai.subut.kurjun.identity.service.RelationManager;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.metadata.common.subutai.TemplateId;
import ai.subut.kurjun.metadata.common.utils.IdValidators;
import ai.subut.kurjun.model.identity.*;
import ai.subut.kurjun.web.service.IdentityManagerService;
import ai.subut.kurjun.web.service.RelationManagerService;
import ai.subut.kurjun.web.service.TemplateManagerService;

import com.google.inject.Inject;

import java.util.Collections;
import java.util.List;
import java.util.Set;


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


    @Override
    public RelationObject toSourceObject( User user )
    {
        if ( user != null )
        {
            RelationObject owner = new DefaultRelationObject();
            owner.setId( user.getKeyId() );
            owner.setClassName( user.getClass().getName() );
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
            targetObject.setId( targetUser.getKeyId() );
            targetObject.setClassName( targetUser.getClass().getName() );
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
            trustObject.setClassName( defaultTemplate.getClass().getName() );
            trustObject.setType( RelationObjectType.RepositoryContent.getId() );
        }

        return trustObject;
    }
}
