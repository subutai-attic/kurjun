package ai.subut.kurjun.core.dao.api;


import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;


/**
 *
 */
public abstract class GenericDAOImpl<T> implements GenericDAO<T>
{

    private static final Logger LOGGER = LoggerFactory.getLogger( GenericDAOImpl.class );


    //**********************************
    @Inject
    Provider<EntityManager> entityManagerProvider;


    public GenericDAOImpl()
    {
        System.currentTimeMillis();
    }


    @Transactional
    public void persist( T entity ) throws DAOException
    {
        try
        {
            entityManagerProvider.get().persist( entity );
        }
        catch ( Exception e )
        {
            LOGGER.error( " ***** GenericDAOImpl Error" + e,e );
            throw new DAOException( e );
        }
    }


    @Transactional
    public T merge( T entity ) throws DAOException
    {
        try
        {
            return entityManagerProvider.get().merge( entity );
        }
        catch ( Exception e )
        {
            LOGGER.error( " ***** GenericDAOImpl Error" + e,e );
            throw new DAOException( e );
        }
    }


    @Transactional
    public void remove( T entity ) throws DAOException
    {
        try
        {
            entityManagerProvider.get().remove( entity );
        }
        catch ( Exception e )
        {
            LOGGER.error( " ***** GenericDAOImpl Error" + e,e );
            throw new DAOException( e );
        }
    }


    @Transactional
    public List<T> findAll( String entityName) throws DAOException
    {
        try
        {
            return entityManagerProvider.get().createQuery( "Select t from " + entityName + " t" )
                                      .getResultList();
        }
        catch ( Exception e )
        {
            LOGGER.error( " ***** GenericDAOImpl Error" + e,e );
            throw new DAOException( e );
        }
    }

    /*
    @Override
    @Transactional
    public T find( Class<? extends T> clazz, ID id ) throws DAOException
    {
        try
        {
            return entityManagerProvider.get().find( clazz, id );
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }
    }*/

    public EntityManager getEntityManager() throws DAOException
    {
        try
        {
            return entityManagerProvider.get();
        }
        catch ( Exception e )
        {
            LOGGER.error( " ***** GenericDAOImpl Error" + e,e );
            throw new DAOException( e );
        }
    }


}