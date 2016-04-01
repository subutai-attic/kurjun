package ai.subut.kurjun.core.dao.api;


import java.util.List;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;


/**
 *
 */
public abstract class GenericDAOImpl<T> implements GenericDAO<T>
{

    //**********************************
    protected Provider<EntityManager> entityManagerProvider;


    @Inject
    public GenericDAOImpl( Provider<EntityManager> entityManagerProvider )
    {
        this.entityManagerProvider = entityManagerProvider;
    }


    @Override
    @Transactional
    public void persist( T entity ) throws DAOException
    {
        try
        {
            entityManagerProvider.get().persist( entity );
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }
    }


    @Override
    @Transactional
    public T merge( T entity ) throws DAOException
    {
        try
        {
            return entityManagerProvider.get().merge( entity );
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }
    }


    @Override
    @Transactional
    public void remove( T entity ) throws DAOException
    {
        try
        {
            entityManagerProvider.get().remove( entity );
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }
    }


    @Override
    @Transactional
    public List<T> findAll( Class<T>  clazz) throws DAOException
    {
        try
        {
            return entityManagerProvider.get().createQuery( "Select t from " + clazz.getClass().getName() + " t" )
                                        .getResultList();
        }
        catch ( Exception e )
        {
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

    @Override
    public EntityManager getEntityManager() throws DAOException
    {
        try
        {
            return entityManagerProvider.get();
        }
        catch ( Exception e )
        {
            throw new DAOException( e );
        }
    }


}