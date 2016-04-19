package ai.subut.kurjun.identity;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.apache.commons.collections.map.HashedMap;

import com.sun.org.glassfish.gmbal.ManagedAttribute;

import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.identity.service.FileDbProvider;
import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.security.manager.SecurityManagerImpl;
import ai.subut.kurjun.security.manager.service.SecurityManager;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class RelationManagerImplTest
{
    private RelationManagerImpl relationManager;
    private Set<Permission> perms = new HashSet<Permission>();

    @Mock
    User user;

    @Mock
    RelationObject relationObject;

    @Mock
    Relation relation;

    @Mock
    SecurityManager securityManager;

    @Mock
    FileDbProvider fileDbProvider;

    @Mock
    FileDb fileDb;


    @Before
    public void setUp() throws Exception
    {
        relationManager = new RelationManagerImpl();

        relationManager.setFileDbProvider( fileDbProvider );
        relationManager.setSecurityManager( securityManager );
    }


    @Test
    public void buildPermissions() throws Exception
    {
        // asserts
        assertNotNull( relationManager.buildPermissions( 4 ) );
        assertEquals( 4, relationManager.buildPermissions( 4 ).size() );

        assertEquals( 0, relationManager.buildPermissions( 0 ).size() );
    }


    @Test
    public void buildPermissionsAllowAll() throws Exception
    {
        perms.add( Permission.Read );
        perms.add( Permission.Delete );
        perms.add( Permission.Update );
        perms.add( Permission.Write );

        // asserts
        assertEquals( 4, relationManager.buildPermissionsAllowAll().size() );
        assertEquals( perms, relationManager.buildPermissionsAllowAll() );
    }


    @Test
    public void buildPermissionsAllowReadWrite() throws Exception
    {
        perms.add( Permission.Read );
        perms.add( Permission.Write );

        assertEquals( 2, relationManager.buildPermissionsAllowReadWrite().size() );
        assertEquals( perms, relationManager.buildPermissionsAllowReadWrite() );
    }


    @Test
    public void buildPermissionsDenyAll() throws Exception
    {
        assertEquals( 0, relationManager.buildPermissionsDenyAll().size() );
    }


    @Test
    public void buildPermissionsDenyDelete() throws Exception
    {
        perms.add( Permission.Update );
        perms.add( Permission.Read );
        perms.add( Permission.Write );

        assertEquals( 3, relationManager.buildPermissionsDenyDelete().size() );
        assertEquals( perms, relationManager.buildPermissionsDenyDelete() );
    }


    @Test
    public void createRelationObject() throws Exception
    {
        assertNotNull( relationManager.createRelationObject( "test", 1 ) );
    }


    @Test
    public void buildTrustRelation() throws Exception
    {
        // mock
        when( user.getKeyFingerprint() ).thenReturn( "fingerprint" );

        assertNotNull( relationManager.buildTrustRelation( user, "targetObject", 1, "trustObject", 1, perms ) );
    }


    @Test
    public void buildTrustRelationFail() throws Exception
    {
        relationManager.buildTrustRelation( user, "targetObject", 1, "trustObject", 1, perms );
    }


    @Test
    public void buildTrustRelation1() throws Exception
    {
        // mock
        when( user.getKeyFingerprint() ).thenReturn( "fingerprint" );

        assertNotNull( relationManager.buildTrustRelation( user, user, "trustObject", 1, perms ) );
    }


    @Test
    public void buildTrustRelation2() throws Exception
    {
        // mock
        when( user.getKeyFingerprint() ).thenReturn( "fingerprint" );

        assertNotNull( relationManager.buildTrustRelation( user, user, relationObject, perms ) );
    }


    @Test
    public void buildTrustRelation3() throws Exception
    {
        // mock
        when( user.getKeyFingerprint() ).thenReturn( "fingerprint" );

        assertNotNull( relationManager
                .buildTrustRelation( "sourceObjectId", 1, "targetObjectId", 1, "trustObjectId", 1, perms ) );
    }


    @Test
    public void saveTrustRelation() throws Exception
    {
        // mock
        when( fileDbProvider.get() ).thenReturn( fileDb );
        when( relation.getId() ).thenReturn( "relationId" );
        when( fileDb.put( anyString(), anyString(), any() ) ).thenReturn( relation );

        assertNotNull( relationManager.saveTrustRelation( relation ) );
    }


    @Test
    public void getRelationFail() throws Exception
    {
        assertNull( relationManager.getRelation( "relationId" ) );
    }


    @Test
    public void getRelation() throws Exception
    {
        when( fileDbProvider.get() ).thenReturn( fileDb );
        when( fileDb.put( anyString(), anyString(), any() ) ).thenReturn( relation );

        relationManager.getRelation( "relationId" );
    }


    @Test
    public void getAllRelationsFail() throws Exception
    {
        assertNull( relationManager.getAllRelations() );
    }


    @Test
    public void getAllRelations() throws Exception
    {
        Map<Object, Object> map = new HashedMap();
        map.put( "test", relation );

        when( fileDbProvider.get() ).thenReturn( fileDb );
        when( fileDb.get( anyString() ) ).thenReturn( map );

        assertFalse( relationManager.getAllRelations().isEmpty() );
    }


    @Test
    public void getAllRelationsIsEmpty() throws Exception
    {
        Map<Object, Object> map = new HashedMap();
        map.put( "test", relation );

        when( fileDbProvider.get() ).thenReturn( fileDb );
        when( fileDb.get( anyString() ) ).thenReturn( null );

        assertNull( relationManager.getAllRelations() );
    }


    @Test
    public void getRelationIsEmpty() throws Exception
    {
        assertNull( relationManager.getRelation( "subjectObjectId", "targetObjectId", "trustedObjectId", 1 ) );
    }


    @Test
    public void getRelation1() throws Exception
    {
        Map<Object, Object> map = new HashedMap();
        map.put( "test", relation );

        when( fileDbProvider.get() ).thenReturn( fileDb );
        when( fileDb.get( anyString() ) ).thenReturn( map );
        when( relation.getTrustObject() ).thenReturn( relationObject );
        relationObject.setId( "trustedObjectId" );
        relationObject.setType( 1 );

        relationManager.getRelation( "subjectObjectId", "targetObjectId", "trustedObjectId", 1 );
    }


    @Test
    public void getRelationsByObject() throws Exception
    {
        relationManager.getRelationsByObject( "trustedObjectId", 1 );
    }


    @Test
    public void getObjectOwner() throws Exception
    {
        relationManager.getObjectOwner( "trustedObjectId", 1 );
    }


    @Test
    public void getRelationsBySourceFail() throws Exception
    {
        assertTrue( relationManager.getRelationsBySource( relationObject ).isEmpty() );
    }


    @Test
    public void getRelationsBySource() throws Exception
    {
        Map<Object, Object> map = new HashedMap();
        map.put( "test", relation );


        when( fileDbProvider.get() ).thenReturn( fileDb );
        when( fileDb.get( anyString() ) ).thenReturn( map );

        relationManager.getRelationsBySource( relationObject );
    }


    @Test
    public void getRelationsByTarget() throws Exception
    {
        Map<Object, Object> map = new HashedMap();
        map.put( "test", relation );


        when( fileDbProvider.get() ).thenReturn( fileDb );
        when( fileDb.get( anyString() ) ).thenReturn( map );

        relationManager.getRelationsByTarget( relationObject );
    }


    @Test
    public void removeRelationFail() throws Exception
    {
        relationManager.removeRelation( "relationId" );
    }


    @Test
    public void removeRelation() throws Exception
    {
        when( fileDbProvider.get() ).thenReturn( fileDb );
        when( fileDb.remove( anyString(), any() ) ).thenReturn( new Object() );

        relationManager.removeRelation( "relationId" );
    }


    @Test
    public void getUserPermissions() throws Exception
    {
        relationManager.getUserPermissions( user,"trustObjectId", 1 );
    }


    @Test
    public void removeRelationsByTrustObject() throws Exception
    {
        relationManager.removeRelationsByTrustObject( "trustObjectId", 1 );
    }
}