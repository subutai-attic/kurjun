package ai.subut.kurjun.identity;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.identity.service.FileDbProvider;
import ai.subut.kurjun.identity.service.RelationManager;
import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.RelationObjectType;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.security.manager.service.SecurityManager;


/**
 *
 */
@Singleton
public class RelationManagerImpl implements RelationManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger( RelationManagerImpl.class );

    @Inject
    SecurityManager securityManager;

    @Inject
    FileDbProvider fileDbProvider;


    //***************************
    public RelationManagerImpl()
    {

    }


    //***************************
    @Override
    public Set<Permission> buildPermissions( int permLevel )
    {
        Set<Permission> perms = new HashSet<Permission>();

        if(permLevel > 0)
            perms.add(  Permission.Read) ;
        if(permLevel > 1)
            perms.add(  Permission.Write) ;
        if(permLevel > 2)
            perms.add(  Permission.Update) ;
        if(permLevel > 3)
            perms.add(  Permission.Delete) ;

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
        try
        {
            RelationObject relationObject = new DefaultRelationObject();

            if ( Strings.isNullOrEmpty( objectId ) )
            {
                relationObject.setId( securityManager.generateUUIDRandom() );
            }
            else
            {
                relationObject.setId( objectId );
            }

            relationObject.setType( objectType );

            return relationObject;
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ******* Error in RelationManager" ,ex );
            return null;
        }
    }


    //***************************
    @Override
    public Relation buildTrustRelation( User user, String targetObjectId, int targetObjectType,
                                        String trustObjectId, int trustObjectType,
                                        Set<Permission> permissions )
    {
        RelationObject sourceObject = createRelationObject( user.getKeyFingerprint(), RelationObjectType.User.getId() );
        RelationObject targetObject = createRelationObject( targetObjectId,  targetObjectType );
        RelationObject trustObject = createRelationObject( trustObjectId,  trustObjectType );

        return buildTrustRelation( sourceObject, targetObject, trustObject ,permissions );
    }


    //***************************
    @Override
    public Relation buildTrustRelation( User sourceUser, User targetUser, String trustObjectId,
                                        int trustObjectType, Set<Permission> permissions )
    {
        RelationObject sourceObject =
                createRelationObject( sourceUser.getKeyFingerprint(), RelationObjectType.User.getId() );
        RelationObject targetObject =
                createRelationObject( targetUser.getKeyFingerprint(), RelationObjectType.User.getId() );
        RelationObject trustObject = createRelationObject( trustObjectId, trustObjectType );

        return buildTrustRelation( sourceObject, targetObject, trustObject, permissions );
    }


    //***************************
    @Override
    public Relation buildTrustRelation( User sourceUser, User targetUser, RelationObject trustObject,
                                        Set<Permission> permissions )
    {
        RelationObject sourceObject =
                createRelationObject( sourceUser.getKeyFingerprint(), RelationObjectType.User.getId() );
        RelationObject targetObject =
                createRelationObject( targetUser.getKeyFingerprint(), RelationObjectType.User.getId() );

        return buildTrustRelation( sourceObject, targetObject, trustObject, permissions );
    }


    //***************************
    @Override
    public Relation buildTrustRelation( String sourceObjectId, int sourceObjectType,
                                        String targetObjectId, int targetObjectType,
                                        String trustObjectId,  int trustObjectType,
                                        Set<Permission> permissions )
    {
        RelationObject sourceObject = createRelationObject( sourceObjectId, sourceObjectType );
        RelationObject targetObject = createRelationObject( targetObjectId, targetObjectType );
        RelationObject trustObject = createRelationObject( trustObjectId,  trustObjectType );

        return buildTrustRelation( sourceObject, targetObject, trustObject, permissions );
    }


    //***************************
    @Override
    public Relation buildTrustRelation( RelationObject source, RelationObject target, RelationObject trustObject,
                                        Set<Permission> permissions )
    {
        try
        {
            if(source != null && target != null && trustObject != null)
            {

                Relation relation = new DefaultRelation();

                relation.setSource( source );
                relation.setTarget( target );
                relation.setTrustObject( trustObject );
                relation.setPermissions( permissions );

                //**************************
                saveTrustRelation( relation );
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
            LOGGER.error( " ******* Error in RelationManager" ,ex );
            return null;
        }
    }


    //***************************
    @Override
    public Relation saveTrustRelation( Relation relation )
    {
        FileDb fileDb = null;
        try
        {
            if(relation != null)
            {
                fileDb = fileDbProvider.get(false);
                fileDb.put( DefaultRelation.MAP_NAME, relation.getId().toLowerCase(), relation );
            }

            return relation;
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Error saving  relation:", ex );
            return null;
        }
        finally
        {
            if ( fileDb != null )
            {
                try
                {
                    fileDb.close();
                }
                catch ( IOException e )
                {
                    LOGGER.warn( "Failed to close fileDB: "+e.getMessage() );
                }
            }
        }
    }


    //********************************************
    @Override
    public Relation getRelation( String relationId )
    {
        FileDb fileDb = null;
        try
        {
            fileDb = fileDbProvider.get(true);
            Relation rel = fileDb.get( DefaultRelation.MAP_NAME, relationId.toLowerCase(), DefaultRelation.class );

            return rel;
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Error getting relation with relationId:" + relationId, ex );
            return null;
        }
        finally
        {
            if ( fileDb != null )
            {
                try
                {
                    fileDb.close();
                }
                catch ( IOException e )
                {
                    LOGGER.warn( "Failed to close fileDB: "+e.getMessage() );
                }
            }
        }
    }

    //********************************************
    @Override
    public List<Relation> getAllRelations()
    {
        FileDb fileDb = null;
        try
        {
            fileDb = fileDbProvider.get(true);
            Map<String, Relation> map = fileDb.get( DefaultRelation.MAP_NAME );
            if ( map != null )
            {
                List<Relation> items = new ArrayList<>( map.values() );

                return items;
            }
            else
            {
                return null;
            }

        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Error getting relation list:" , ex );
            return null;
        }
        finally
        {
            if ( fileDb != null )
            {
                try
                {
                    fileDb.close();
                }
                catch ( IOException e )
                {
                    LOGGER.warn( "Failed to close fileDB: "+e.getMessage() );
                }
            }
        }
    }


        //********************************************
        @Override
        public Relation getRelation( String sourceObjectId, String targetObjectId, String trustedObjectId,
                                     int objectId )
        {
            try
            {
                RelationObject obj = createRelationObject( trustedObjectId, objectId );
                List<Relation> relations = getRelationsByObject( obj );

                for(Relation relation:relations)
                {
                    if(relation.getSource().getId().equals( sourceObjectId ) &&
                            relation.getTarget().getId().equals( sourceObjectId ))
                    {
                        return relation;
                    }
                }

                return null;

            }
            catch ( Exception ex )
            {
                LOGGER.error( " ***** Error getting relation with sourceId:" + sourceObjectId, ex );
                return null;
            }
    }


    //***************************
    @Override
    public List<Relation> getRelationsByObject( final RelationObject trustObject )
    {
        FileDb fileDb = null;
        try
        {
            fileDb = fileDbProvider.get(true);
            Map<String, Relation> map = fileDb.get( DefaultRelation.MAP_NAME );

            return map.values().stream().filter( r -> r.getTrustObject().equals(trustObject)).collect(Collectors.toList());
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Failed to get relations by this TrustObject: " + trustObject.getId(), ex );
        }
        finally
        {
            if ( fileDb != null )
            {
                try
                {
                    fileDb.close();
                }
                catch ( IOException e )
                {
                    LOGGER.warn( "Failed to close fileDB: "+e.getMessage() );
                }
            }
        }

        return Collections.emptyList();
    }


    //***************************
    @Override
    public List<Relation> getRelationsByObject( String trustObjectId, int trustObjectType )
    {
        try
        {
            return getRelationsByObject(createRelationObject( trustObjectId, trustObjectType ));
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
            List<Relation> relations = getRelationsByObject(createRelationObject( trustObjectId, trustObjectType ));

            for(Relation relation: relations)
            {
                if(relation.getSource().getId().equals( relation.getTarget().getId()))
                {
                    return relation;
                }
            }

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
        FileDb fileDb = null;
        try
        {
            fileDb = fileDbProvider.get(true);
            Map<String, Relation> map = fileDb.get( DefaultRelation.MAP_NAME );

            return map.values().parallelStream().filter( r -> r.getSource().equals(sourceObject)).collect(Collectors.toList());
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Failed to get relations by this SourceObject: " + sourceObject.getId(), ex );
        }
        finally
        {
            if ( fileDb != null )
            {
                try
                {
                    fileDb.close();
                }
                catch ( IOException e )
                {
                    LOGGER.warn( "Failed to close fileDB: "+e.getMessage() );
                }
            }
        }

        return Collections.emptyList();
    }


    //***************************
    @Override
    public List<Relation> getRelationsByTarget( final RelationObject targetObject )
    {
        FileDb fileDb = null;
        try
        {
            fileDb = fileDbProvider.get(true);
            Map<String, Relation> map = fileDb.get( DefaultRelation.MAP_NAME );

            return map.values().stream().filter( r -> r.getTarget().equals(targetObject)).collect(Collectors.toList());
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Failed to get relations by this TargetObject: " + targetObject.getId(), ex );
        }
        finally
        {
            if ( fileDb != null )
            {
                try
                {
                    fileDb.close();
                }
                catch ( IOException e )
                {
                    LOGGER.warn( "Failed to close fileDB: "+e.getMessage() );
                }
            }
        }

        return Collections.emptyList();
    }


    //***************************
    @Override
    public void removeRelation( final String relationId )
    {
        FileDb fileDb = null;
        try
        {
            fileDb = fileDbProvider.get(false);
            fileDb.remove( DefaultRelation.MAP_NAME, relationId );
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Failed to remove this relation: " + relationId, ex );
        }
        finally
        {
            if ( fileDb != null )
            {
                try
                {
                    fileDb.close();
                }
                catch ( IOException e )
                {
                    LOGGER.warn( "Failed to close fileDB: "+e.getMessage() );
                }
            }
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

            for(Relation relation: relations)
            {
                if(relation.getTarget().getId().equals( target.getKeyFingerprint()) )
                {
                    perms.addAll( relation.getPermissions() );
                }
                else if(relation.getTarget().getId().equals( IdentityManagerImpl.PUBLIC_USER_ID) )
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
            List<Relation> relations = getRelationsByObject( createRelationObject( trustObjectId, trustObjectType ) );

            for(Relation relation: relations)
            {
                removeRelation( relation.getId() );
            }
        }
        catch ( Exception ex )
        {
            LOGGER.error( " ***** Failed to remove trustedObjects: " + trustObjectId, ex );
        }
    }
}
