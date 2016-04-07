package ai.subut.kurjun.identity;


import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.core.dao.model.identity.RelationEntity;
import ai.subut.kurjun.core.dao.model.identity.RelationObjectEntity;
import ai.subut.kurjun.core.dao.service.identity.RelationDataService;
import ai.subut.kurjun.identity.service.RelationManager;
import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.ObjectType;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.security.manager.service.SecurityManager;


/**
 *
 */
@Singleton
public class RelationManagerImpl implements RelationManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger( RelationManagerImpl.class );

    private SecurityManager securityManager = null;
    private RelationDataService relationDataService = null;


    //***************************
    @Inject
    public RelationManagerImpl( RelationDataService relationDataService, SecurityManager securityManager )
    {
        this.relationDataService = relationDataService;
        this.securityManager = securityManager;
    }


    //***************************
    @Override
    public Set<Permission> buildPermissions( int permLevel )
    {
        Set<Permission> perms = new HashSet<Permission>();

        if ( permLevel > 0 )
        {
            perms.add( Permission.Read );
        }
        if ( permLevel > 1 )
        {
            perms.add( Permission.Write );
        }
        if ( permLevel > 2 )
        {
            perms.add( Permission.Update );
        }
        if ( permLevel > 3 )
        {
            perms.add( Permission.Delete );
        }

        return perms;
    }


    //***************************
    @Override
    public Set<Permission> buildPermissionsAllowAll()
    {
        return buildPermissions( 4 );
    }


    //***************************
    @Override
    public Set<Permission> buildPermissionsAllowReadWrite()
    {
        return buildPermissions( 2 );
    }


    //***************************
    @Override
    public Set<Permission> buildPermissionsDenyAll()
    {
        return buildPermissions( 0 );
    }


    //***************************
    @Override
    public Set<Permission> buildPermissionsDenyDelete()
    {
        return buildPermissions( 3 );
    }


    //***************************
    @Override
    public RelationObject createRelationObject( String objectId, int objectType )
    {
        RelationObject relationObject = null;

        try
        {
            //----------------------------------------
            relationObject = new RelationObjectEntity();

            if ( Strings.isNullOrEmpty( objectId ) )
            {
                relationObject.setObjectId( securityManager.generateUUIDRandom() );
            }
            else
            {
                relationObject.setObjectId( objectId );
            }

            relationObject.setType( objectType );


        }
        catch ( Exception ex )
        {
            LOGGER.error( " ******* Error in RelationManager", ex );
            return null;
        }

        return relationObject;
    }


    //***************************
    @Override
    public Relation buildTrustRelation( User user, String targetObjectId, int targetObjectType, String trustObjectId,
                                        int trustObjectType, Set<Permission> permissions )
    {
        RelationObject sourceObject = createRelationObject( user.getKeyFingerprint(), ObjectType.User.getId() );
        RelationObject targetObject = createRelationObject( targetObjectId, targetObjectType );
        RelationObject trustObject = createRelationObject( trustObjectId, trustObjectType );

        return buildTrustRelation( sourceObject, targetObject, trustObject, permissions );
    }


    //***************************
    @Override
    public Relation buildTrustRelation( User sourceUser, User targetUser, String trustObjectId, int trustObjectType,
                                        Set<Permission> permissions )
    {
        RelationObject sourceObject =
                createRelationObject( sourceUser.getKeyFingerprint(), ObjectType.User.getId() );
        RelationObject targetObject =
                createRelationObject( targetUser.getKeyFingerprint(), ObjectType.User.getId() );
        RelationObject trustObject = createRelationObject( trustObjectId, trustObjectType );

        return buildTrustRelation( sourceObject, targetObject, trustObject, permissions );
    }


    //***************************
    @Override
    public Relation buildTrustRelation( User sourceUser, User targetUser, RelationObject trustObject,
                                        Set<Permission> permissions )
    {
        RelationObject sourceObject =
                createRelationObject( sourceUser.getKeyFingerprint(), ObjectType.User.getId() );
        RelationObject targetObject =
                createRelationObject( targetUser.getKeyFingerprint(), ObjectType.User.getId() );

        return buildTrustRelation( sourceObject, targetObject, trustObject, permissions );
    }


    //***************************
    @Override
    public Relation buildTrustRelation( User sourceUser, RelationObject targetObject, RelationObject trustObject,
                                        Set<Permission> permissions )
    {
        RelationObject sourceObject =
                createRelationObject( sourceUser.getKeyFingerprint(), ObjectType.User.getId() );

        return buildTrustRelation( sourceObject, targetObject, trustObject, permissions );
    }


    //***************************
    @Override
    public Relation buildTrustRelation( String sourceObjectId, int sourceObjectType, String targetObjectId,
                                        int targetObjectType, String trustObjectId, int trustObjectType,
                                        Set<Permission> permissions )
    {
        RelationObject sourceObject = createRelationObject( sourceObjectId, sourceObjectType );
        RelationObject targetObject = createRelationObject( targetObjectId, targetObjectType );
        RelationObject trustObject = createRelationObject( trustObjectId, trustObjectType );

        return buildTrustRelation( sourceObject, targetObject, trustObject, permissions );
    }



    //***************************
    @Override
    public Relation buildTrustRelation( RelationObject source, RelationObject target, RelationObject trustObject,
                                        Set<Permission> permissions )
    {
        try
        {

            if ( source != null && target != null && trustObject != null )
            {

                Relation relation = new RelationEntity();

                //-------------------------------
                //RelationObject sourceObj = getRelationObject(source.getObjectId(),source.getType());


                relation.setSource( source );
                relation.setTarget( target );
                relation.setTrustObject( trustObject );
                relation.setPermissions( permissions );

                //**************************
                relationDataService.persistRelation( relation );
                //**************************

                return relation;
            }
            else
            {
                return null;
            }
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ******* Error in RelationManager", ex );
            return null;
        }
    }


    //********************************************
    @Override
    public Relation getRelation( long relationId )
    {
        try
        {
            return relationDataService.getRelation( relationId );
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Error getting relation with relationId:" + relationId, ex );
            return null;
        }
    }


    //********************************************
    @Override
    public RelationObject getRelationObject( String id, int type )
    {
        try
        {
            return relationDataService.getRelationObject( id ,type );
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Error getting relationObject with relationObjectId:" + id, ex );
            return null;
        }
    }


    //********************************************
    @Override
    public List<Relation> getAllRelations()
    {
        try
        {
            return relationDataService.getAllRelations();
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Error getting relation list:", ex );
            return null;
        }
    }


    //***************************
    @Override
    public List<Relation> getRelationsByObject( final RelationObject trustObject )
    {
        try
        {
            return relationDataService.getRelationsByTrustObject( trustObject );
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Failed to get relations by this TrustObject: " + trustObject.getObjectId(), ex );
        }

        return Collections.emptyList();
    }


    //***************************
    @Override
    public List<Relation> getRelationsByObject( String trustObjectId, int trustObjectType )
    {
        try
        {
            return getRelationsByObject( createRelationObject( trustObjectId, trustObjectType ) );
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Failed to get relations by this TrustObject: " + trustObjectId, ex );
        }

        return Collections.emptyList();
    }


    //***************************
    @Override
    public Relation getObjectOwner( String trustObjectId, int trustObjectType )
    {
        try
        {
           return relationDataService.getTrustObjectOwner( trustObjectId, trustObjectType );
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Failed to get relations by this TrustObject: " + trustObjectId, ex );
        }

        return null;
    }


    //***************************
    @Override
    public List<Relation> getRelationsBySource( final RelationObject sourceObject )
    {
        try
        {
            return relationDataService.getRelationsBySource( sourceObject );
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Failed to get relations by this SourceObject: " + sourceObject.getObjectId(), ex );
        }

        return Collections.emptyList();
    }


    //***************************
    @Override
    public List<Relation> getRelationsByTarget( final RelationObject targetObject )
    {
        try
        {
            return relationDataService.getRelationsByTarget( targetObject );
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Failed to get relations by this TargetObject: " + targetObject.getObjectId(), ex );
        }

        return Collections.emptyList();
    }


    //***************************
    @Override
    public void removeRelation( final long relationId )
    {
        try
        {
            relationDataService.removeRelation( relationId );
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Failed to remove this relation: " + relationId, ex );
        }
    }


    //***************************
    @Override
    public Set<Permission> getUserPermissions( User target, String trustObjectId, int trustObjectType )
    {
        Set<Permission> perms = buildPermissionsDenyAll();

        try
        {
            List<Relation> relations = getRelationsByObject( createRelationObject( trustObjectId, trustObjectType ) );

            for ( Relation relation : relations )
            {
                if ( relation.getTarget().getObjectId().equals( target.getKeyFingerprint() ) )
                {
                    perms.addAll( relation.getPermissions() );
                }
                else if ( relation.getTarget().getObjectId().equals( IdentityManagerImpl.PUBLIC_USER_ID ) )
                {
                    perms.addAll( relation.getPermissions() );
                }
            }
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Failed to find trustedObject: " + trustObjectId, ex );
        }

        return perms;
    }


    //***************************
    @Override
    public void removeRelationsByTrustObject( String trustObjectId, int trustObjectType )
    {
        try
        {
            relationDataService.removeByTrustObject( trustObjectId, trustObjectType );
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Failed to remove trustedObjects: " + trustObjectId, ex );
        }
    }


    //***************************
    @Override
    public int setObjectOwner( User owner, String objectId, int objectType )
    {
        Relation relation = getObjectOwner( objectId, objectType );

        if ( relation == null )
        {
            buildTrustRelation( owner, owner, objectId, objectType, buildPermissions( Permission.Delete.getId() ) );
        }

        return 0;
    }



    //*******************************************************************
    @Override
    public boolean checkObjectPermissions( User user, String parentId, int parentType, String childId, int childType,
                                           Permission perm )
    {
        boolean access = false;

        if ( getUserPermissions( user, parentId, parentType ).contains( perm ) )
        {
            access = true;
        }

        if ( !Strings.isNullOrEmpty( childId ) )
        {

            if ( !access)
            {
                if ( getUserPermissions( user, childId, childType ).contains( perm ) )
                {
                    access = true;
                }
            }
        }
        return access;
    }

}
