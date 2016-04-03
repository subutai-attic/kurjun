package ai.subut.kurjun.core.dao.api.identity;


import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import com.google.inject.persist.Transactional;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.GenericDAOImpl;
import ai.subut.kurjun.core.dao.model.identity.RelationEntity;
import ai.subut.kurjun.model.identity.Relation;


/**
 *
 */
public class RelationDAO extends GenericDAOImpl<Relation>
{

    public RelationDAO()
    {
        super();
    }


    //***********************************************
    @Transactional
    public Relation find( long id ) throws DAOException
    {
        try
        {
            return getEntityManager().find( RelationEntity.class, id );
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }
    }


    //***********************************************
    @Transactional
    public List<Relation> findBySourceObject( String objectId, int objectType ) throws DAOException
    {
        try
        {
            Query qr = getEntityManager().createQuery(
                    " select e from RelationEntity e where e.source.id=:objectId and e.source.type=:objectType",
                    RelationEntity.class );
            qr.setParameter( "objectId" ,objectId );
            qr.setParameter( "objectType" ,objectType );

            List<Relation>  relations = qr.getResultList();
            
            if(!relations.isEmpty())
                return relations;
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }

        return Collections.emptyList();

    }


    //***********************************************
    @Transactional
    public List<Relation> findByTargetObject( String objectId, int objectType ) throws DAOException
    {
        try
        {
            Query qr = getEntityManager().createQuery(
                    " select e from RelationEntity e where e.target.id=:objectId and e.target.type=:objectType",
                    RelationEntity.class );
            qr.setParameter( "objectId" ,objectId );
            qr.setParameter( "objectType" ,objectType );

            List<Relation>  relations = qr.getResultList();

            if(!relations.isEmpty())
                return relations;
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }

        return Collections.emptyList();

    }


    //***********************************************
    @Transactional
    public List<Relation> findByTrustObject( String objectId, int objectType ) throws DAOException
    {
        try
        {
            Query qr = getEntityManager().createQuery(
                    " select e from RelationEntity e where e.trustObject.id=:objectId and e.trustObject.type=:objectType",
                    RelationEntity.class );
            qr.setParameter( "objectId" ,objectId );
            qr.setParameter( "objectType" ,objectType );

            List<Relation>  relations = qr.getResultList();

            if(!relations.isEmpty())
                return relations;
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }

        return Collections.emptyList();

    }

    @Transactional
    //***********************************************
    public void removeByTrustObject( String objectId, int objectType ) throws DAOException
    {
        try
        {
            Query qr = getEntityManager().createQuery(
                    " delete from RelationEntity e where e.trustObject.id=:objectId and e.trustObject.type=:objectType",
                    RelationEntity.class );
            qr.setParameter( "objectId" ,objectId );
            qr.setParameter( "objectType" ,objectType );

            qr.executeUpdate();

        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }
    }


    //***********************************************
    @Transactional
    public Relation findTrustObjectOwner( String objectId, int objectType ) throws DAOException
    {
        try
        {
            Query qr = getEntityManager().createQuery(
                    " select e from RelationEntity e where e.trustObject.id=:objectId and e.trustObject.type=:objectType"
                            + " and e.source.id=e.target.id"
                            + " and e.source.type=e.target.type", RelationEntity.class );

            qr.setParameter( "objectId" ,objectId );
            qr.setParameter( "objectType" ,objectType );

            Relation relation = (RelationEntity)qr.getSingleResult();

            return relation;
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }
    }

}
