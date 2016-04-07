package ai.subut.kurjun.core.dao.api.metadata;


import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.GenericDAOImpl;
import ai.subut.kurjun.core.dao.model.metadata.RawDataEntity;
import ai.subut.kurjun.model.metadata.raw.RawData;
import ai.subut.kurjun.model.repository.ArtifactId;


public class RawDAO extends GenericDAOImpl<RawData>
{
    private static final Logger LOGGER = LoggerFactory.getLogger( RawDAO.class );


    public RawDAO()
    {
        super();
    }

    public RawDAO(EntityManagerFactory emf)
    {
        super(emf);
    }


    @Transactional
    public RawData find( ArtifactId id ) throws DAOException
    {
        try
        {
            EntityManager em = getEntityManager();
            return em.find( RawDataEntity.class, id );
        }
        catch ( Exception e )
        {
            LOGGER.error( "****** Error in RawDAO find :" + e.getMessage(), e );
            throw new DAOException( e );
        }
    }


    //***********************************************
    @Transactional
    public List<RawData> findByRepository( String repoContext, int repoType ) throws DAOException
    {
        try
        {
            Query qr = getEntityManager().createQuery(
                    " select e from RawDataEntity e where e.id.context=:repoContext and e.id.type=:repoType",
                    RawDataEntity.class );
            qr.setParameter( "repoContext" ,repoContext );
            qr.setParameter( "repoType" ,repoType );

            List<RawData>  templates = qr.getResultList();

            if(!templates.isEmpty())
                return templates;
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }

        return Collections.emptyList();

    }

}
