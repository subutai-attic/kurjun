package ai.subut.kurjun.core.dao.api.torrent;


import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.GenericDAOImpl;
import ai.subut.kurjun.core.dao.model.identity.UserEntity;
import ai.subut.kurjun.core.dao.model.metadata.KurjunTorrent;
import ai.subut.kurjun.model.identity.User;


public class TorrentDAO extends GenericDAOImpl<KurjunTorrent>
{

    private static final Logger LOGGER = LoggerFactory.getLogger( TorrentDAO.class );



    public TorrentDAO()
    {
        super();
    }


    @Transactional
    public User find( String fingerprint ) throws DAOException
    {
        try
        {
            EntityManager em = getEntityManager();
            return em.find( UserEntity.class, fingerprint );
        }
        catch ( Exception e )
        {
            LOGGER.error( "****** Error in UserDAO find :" + e, e );
            throw new DAOException( e );
        }
    }
}
