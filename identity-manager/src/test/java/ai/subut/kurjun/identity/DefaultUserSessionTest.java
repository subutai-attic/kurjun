package ai.subut.kurjun.identity;


import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserToken;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class DefaultUserSessionTest
{
    private DefaultUserSession userSession;

    @Mock
    User user;

    @Mock
    UserToken userToken;


    @Before
    public void setUp() throws Exception
    {
        userSession = new DefaultUserSession();

        userSession.setUser( user );
        userSession.setUserToken( userToken );
        userSession.setStatus( 1 );
        userSession.setStartDate( new Date() );
        userSession.setEndDate( new Date(  ) );
    }


    @Test
    public void getUser() throws Exception
    {
        assertNotNull( userSession.getUser() );
    }


    @Test
    public void getUserToken() throws Exception
    {
        assertNotNull( userSession.getUserToken() );
    }


    @Test
    public void getStatus() throws Exception
    {
        assertNotNull( userSession.getStatus() );
    }


    @Test
    public void getStartDate() throws Exception
    {
        assertNotNull( userSession.getStartDate() );
    }


    @Test
    public void getEndDate() throws Exception
    {
        assertNotNull( userSession.getEndDate() );
    }
}