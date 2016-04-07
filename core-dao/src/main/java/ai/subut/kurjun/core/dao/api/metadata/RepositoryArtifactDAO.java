package ai.subut.kurjun.core.dao.api.metadata;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.GenericDAOImpl;
import ai.subut.kurjun.core.dao.model.metadata.RepositoryArtifactEntity;
import ai.subut.kurjun.core.dao.model.metadata.RepositoryArtifactId;
import ai.subut.kurjun.model.repository.RepositoryArtifact;


/**
 *
 */
public class RepositoryArtifactDAO   extends GenericDAOImpl<RepositoryArtifact>
{
    private static final Logger LOGGER = LoggerFactory.getLogger( RawDAO.class );


    public RepositoryArtifactDAO()
    {
        super();
    }

    public RepositoryArtifactDAO(EntityManagerFactory emf)
    {
        super(emf);
    }



    @Transactional
    public RepositoryArtifact find( RepositoryArtifactId id ) throws DAOException
    {
        try
        {
            EntityManager em = getEntityManager();
            return em.find( RepositoryArtifactEntity.class , id );
        }
        catch ( Exception e )
        {
            LOGGER.error( "****** Error in UserDAO find :" + e, e );
            throw new DAOException( e );
        }
    }
}
