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
import ai.subut.kurjun.core.dao.model.metadata.RepositoryDataEntity;
import ai.subut.kurjun.core.dao.model.metadata.RepositoryDataId;
import ai.subut.kurjun.model.metadata.RepositoryData;


/**
 *
 */
public class RepositoryDAO  extends GenericDAOImpl<RepositoryData>
{
    private static final Logger LOGGER = LoggerFactory.getLogger( RawDAO.class );


    public RepositoryDAO()
    {
        super();
    }

    public RepositoryDAO(EntityManagerFactory emf)
    {
        super(emf);
    }


    //***********************************************
    public RepositoryData find( RepositoryDataId id ) throws DAOException
    {
        try
        {
            EntityManager em = getEntityManager();
            return em.find( RepositoryDataEntity.class , id );
        }
        catch ( Exception e )
        {
            LOGGER.error( "****** Error in UserDAO find :" + e, e );
            throw new DAOException( e );
        }
    }


    //***********************************************
    public List<RepositoryData> findByRepository( int repoType ) throws DAOException
    {
        try
        {
            Query qr = getEntityManager().createQuery(" select e from RepositoryDataEntity e where e.id.type=:repoType",
                                                        RepositoryData.class );
            qr.setParameter( "repoType" ,repoType );

            List<RepositoryData>  templates = qr.getResultList();

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
