package ai.subut.kurjun.core.dao.api;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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
    Provider<EntityManager> emp;

    //**********************************
    private EntityManagerFactory emf;


    public GenericDAOImpl()
    {
    }

    public GenericDAOImpl( EntityManagerFactory emf)
    {
        this.emf = emf;
    }


    @Transactional
    public void persist( T entity ) throws DAOException
    {
        try
        {
            getEntityManager().persist( entity );
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
            return getEntityManager().merge( entity );
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
            getEntityManager().remove( entity );
        }
        catch ( Exception e )
        {
            LOGGER.error( " ***** GenericDAOImpl Error" + e,e );
            throw new DAOException( e );
        }
    }


    public List<T> findAll( String entityName) throws DAOException
    {
        try
        {
            return getEntityManager().createQuery( "Select t from " + entityName + " t" )
                                      .getResultList();
        }
        catch ( Exception e )
        {
            LOGGER.error( " ***** GenericDAOImpl Error" + e,e );
            throw new DAOException( e );
        }
    }


    public EntityManager getEntityManager() throws DAOException
    {
        try
        {
            if(emp != null)
            {
                return emp.get();
            }
            else if(emf != null)
            {
                return emf.createEntityManager();
            }
            else
            {
                return null;
            }

        }
        catch ( Exception e )
        {
            LOGGER.error( " ***** GenericDAOImpl Error" + e,e );
            throw new DAOException( e );
        }
    }

}