package ai.subut.kurjun.core.dao.api.metadata;


import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.GenericDAOImpl;
import ai.subut.kurjun.core.dao.model.metadata.AptDataEntity;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;


public class AptDAO extends GenericDAOImpl<PackageMetadata>
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AptDAO.class );


    public AptDAO()
    {
        super();
    }

    @Transactional
    public PackageMetadata find( String id ) throws DAOException
    {
        try
        {
            EntityManager em = getEntityManager();
            return em.find( AptDataEntity.class, id );
        }
        catch ( Exception e )
        {
            LOGGER.error( "****** Error in AptDAO find :" + e.getMessage(), e );
            throw new DAOException( e );
        }
    }
}
