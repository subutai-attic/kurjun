package ai.subut.kurjun.identity;


import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class DefaultUserTokenTest
{
    private DefaultUserToken userToken;


    @Before
    public void setUp() throws Exception
    {
        userToken = new DefaultUserToken();

        userToken.setSecret( "secret" );
        userToken.setIssuer( "issuer" );
        userToken.setHashAlgorithm( "algo" );
        userToken.setToken( "token" );
        userToken.setValidDate( new Date() );
    }


    @Test
    public void getHeader() throws Exception
    {
        assertNotNull( userToken.getHeader() );
    }


    @Test
    public void getClaims() throws Exception
    {
        assertNotNull( userToken.getClaims() );
    }


    @Test
    public void getFullToken() throws Exception
    {
        assertNotNull( userToken.getFullToken() );
    }


    @Test
    public void getToken() throws Exception
    {
        assertNotNull( userToken.getToken() );
    }


    @Test
    public void getSecret() throws Exception
    {
        assertNotNull( userToken.getSecret() );
    }


    @Test
    public void getHashAlgorithm() throws Exception
    {
        assertNotNull( userToken.getHashAlgorithm() );
    }


    @Test
    public void getIssuer() throws Exception
    {
        assertNotNull( userToken.getIssuer() );
    }


    @Test
    public void getValidateDate()
    {
        assertNotNull( userToken.getValidDate() );
    }


    @Test
    public void testInvalidCase()
    {
        userToken.setValidDate( null );
        assertNotNull( userToken.getClaims() );
    }
}