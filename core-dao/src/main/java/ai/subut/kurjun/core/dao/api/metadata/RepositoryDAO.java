package ai.subut.kurjun.core.dao.api.metadata;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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


    @Transactional
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
}
