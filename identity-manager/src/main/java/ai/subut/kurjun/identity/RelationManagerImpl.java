package ai.subut.kurjun.identity;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.identity.service.FileDbProvider;
import ai.subut.kurjun.identity.service.RelationManager;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.RelationObject;


/**
 *
 */
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
    public Relation buildTrustRelation( RelationObject source, RelationObject target, RelationObject trustObject )
    {
        try
        {
            Relation relation = new DefaultRelation();

            relation.setSource( source );
            relation.setTarget( target );
            relation.setTrustObject( trustObject );

            return relation;

        }
        catch(Exception ex)
        {
            return null;
        }

    }


    //***************************
    @Override
    public Relation saveTrustRelation( Relation relation )
    {
        try
        {
            FileDb fileDb = fileDbProvider.get();
            fileDb.put( DefaultRelation.MAP_NAME, relation.getId().toLowerCase(), relation );

            return relation;

        }
        catch(Exception ex)
        {
            LOGGER.error( " ***** Error saving  relation:",ex);
            return null;
        }
    }



    //********************************************
    @Override
    public Relation getRelation( String relationId )
    {
        try
        {
            FileDb fileDb = fileDbProvider.get();
            return fileDb.get( DefaultRelation.MAP_NAME, relationId.toLowerCase(), DefaultRelation.class );
        }
        catch(Exception ex)
        {
            LOGGER.error( " ***** Error getting relation with relationId:" + relationId,ex);
            return null;
        }
    }



    //***************************
    @Override
    public List<Relation> getRelationsByObject( final RelationObject trustObject )
    {
        return null;
    }


    //***************************
    @Override
    public List<Relation> getRelationsBySource( final RelationObject sourceObject )
    {
        return null;
    }


    //***************************
    @Override
    public List<Relation> getRelationsByTarget( final RelationObject targetObject )
    {
        return null;
    }


    //***************************
    @Override
    public void removeRelation( final String relationId )
    {

    }

}
