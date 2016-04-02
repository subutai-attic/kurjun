package ai.subut.kurjun.core.dao;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.GenericDAOImpl;
import ai.subut.kurjun.core.dao.api.KurjunDAOModule;
import ai.subut.kurjun.core.dao.model.identity.UserEntity;
import ai.subut.kurjun.model.identity.User;


/**
 *
 */
public class GenericDAOTest
{
    @Inject
    GenericDAOImpl<User> dao;


    @Before
    public void setUp() throws Exception
    {
        Injector injector = Guice.createInjector( new KurjunDAOModule() );
        injector.injectMembers( this );
        dao.getEntityManager().getTransaction().begin();
    }


    @After
    public void tearDown() throws Exception
    {
        dao.getEntityManager().getTransaction().rollback();
    }


    @Test
    public void testInsert()
    {
        User user = new UserEntity();
        user.setKeyFingerprint( "FG-TEST" );
        user.setUserName( "USERNAME" );

        try
        {
            dao.persist( user );
        }
        catch ( DAOException e )
        {
            e.printStackTrace();
        }
    }
}