package ai.subut.kurjun.core.dao.api;


import java.util.List;

import javax.persistence.EntityManager;


/**
 *
 */
public interface GenericDAO<T>
{
    /**
     *
     */
    void persist(T entity) throws DAOException;


    /**
     *
     */
    T merge(T entity) throws DAOException;


    /**
     *
     */
    void remove(T entity) throws DAOException;


    /**
     *
     */
    List<T> findAll(T entity) throws DAOException;


    /**
     *
     */
    public EntityManager getEntityManager() throws DAOException;


}
