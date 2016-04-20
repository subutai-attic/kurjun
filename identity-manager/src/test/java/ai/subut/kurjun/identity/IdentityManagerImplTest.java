package ai.subut.kurjun.identity;


import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.apache.commons.collections.map.HashedMap;
import org.apache.cxf.common.i18n.Exception;


import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.identity.service.FileDbProvider;
import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.security.manager.service.SecurityManager;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class IdentityManagerImplTest
{
    private IdentityManagerImpl identityManager;

    @Mock
    FileDbProvider fileDbProvider;

    @Mock
    FileDb fileDb;

    @Mock
    DefaultUser defaultUser;

    @Mock
    User user;

    @Mock
    RelationObject relationObject;

    @Mock
    SecurityManager securityManager;

    @Mock
    PGPPublicKey pgpPublicKey;

    @Mock
    Iterator iterator;


    @Before
    public void setUp() throws Exception
    {
        // mock
        when( fileDbProvider.get() ).thenReturn( fileDb );

        identityManager = new IdentityManagerImpl( fileDbProvider );
    }


    @Test
    public void getRelationManager() throws Exception
    {
        identityManager.getRelationManager();
    }


    @Test
    public void getPublicUser() throws Exception
    {
        // mock
        when( fileDb.get( anyString(), anyString(), any() ) ).thenReturn( defaultUser );

        assertNotNull( identityManager.getPublicUser() );
    }


    @Test
    public void getPublicUserId() throws Exception
    {
        assertNotNull( identityManager.getPublicUserId() );
    }


    @Test
    public void loginPublicUser() throws Exception
    {
        assertNotNull( identityManager.loginPublicUser() );
    }


    @Test
    public void testExceptionGetUser() throws Exception
    {
        // mock
        when( fileDbProvider.get() ).thenThrow( java.lang.Exception.class );

        assertNotNull( identityManager.loginPublicUser() );
    }


    @Test
    public void loginUserFail() throws Exception
    {
        assertNull( identityManager.loginUser( "token", "test" ) );
    }


    @Test
    public void loginUser() throws Exception
    {
        // mock
        when( fileDb.get( anyString(), anyString(), any() ) ).thenReturn( user );

        assertNull( identityManager.loginUser( "test", "test" ) );
    }


    @Test
    public void authenticateUser() throws Exception
    {
        identityManager.authenticateUser( "test", DefaultUserTest.KEY );
    }


    @Test
    public void authenticateByToken() throws Exception
    {
        when( fileDb.get( anyString(), anyString(), any() ) ).thenReturn( user );

        identityManager.authenticateByToken( "token" );
    }


    @Test
    public void getSystemOwner() throws Exception
    {
        Map<Object, Object> map = new HashedMap();
        map.put( "test", user );

        when( fileDb.get( anyString() ) ).thenReturn( map );
        when( user.getType() ).thenReturn( 3 );

        assertNotNull( identityManager.getSystemOwner() );
    }


    @Test
    public void getSystemOwnerNotFound() throws Exception
    {
        Map<Object, Object> map = new HashedMap();
        map.put( "test", user );

        when( fileDb.get( anyString() ) ).thenReturn( map );

        assertNull( identityManager.getSystemOwner() );
    }


    @Test
    public void setSystemOwner() throws Exception
    {
        identityManager.setSystemOwner( "test", DefaultUserTest.KEY );
        identityManager.setSystemOwner( null, DefaultUserTest.KEY );
    }


    @Test
    public void addUser() throws Exception, PGPException
    {
        identityManager.addUser( DefaultUserTest.KEY );
    }


    @Test
    public void addUser1() throws Exception
    {

    }


    @Test
    public void saveUser() throws Exception
    {
        // mock
        when( user.getKeyFingerprint() ).thenReturn( "fingerprint" );
        when( fileDb.put( anyString(), anyString(), any() ) ).thenReturn( user );

        // asserts
        assertNotNull( identityManager.saveUser( user ) );
    }


    @Test
    public void saveUserException() throws Exception
    {
        when( fileDbProvider.get() ).thenThrow( Exception.class );

        identityManager.saveUser( user );
    }


    @Test
    public void getAllUsers() throws Exception
    {
        identityManager.getAllUsers();
    }


    @Test
    public void getAllUsersReturnNull() throws Exception
    {
        when( fileDb.get( anyString() ) ).thenReturn( null );

        identityManager.getAllUsers();
    }


    @Test
    public void getAllUsersException() throws Exception
    {
        when( fileDb.get( anyString() ) ).thenThrow( Exception.class );

        identityManager.getAllUsers();
    }


    @Test
    public void createUserToken() throws Exception
    {
        // asserts
        assertNotNull( identityManager.createUserToken( user, "token", "secret", "issue", new Date() ) );
        assertNotNull( identityManager.createUserToken( user, null, null, null, null ) );
    }


    @Test
    public void hasPermmission() throws Exception
    {
        // asserts
        assertTrue( identityManager.hasPermmission( user, relationObject, null ) );
    }
}