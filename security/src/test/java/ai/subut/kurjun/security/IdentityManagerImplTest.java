package ai.subut.kurjun.security;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import org.apache.commons.io.IOUtils;

import ai.subut.kurjun.model.security.Group;
import ai.subut.kurjun.model.security.Identity;
import ai.subut.kurjun.model.security.Permission;
import ai.subut.kurjun.security.service.FileDbProvider;
import ai.subut.kurjun.security.service.GroupManager;
import ai.subut.kurjun.security.service.IdentityManager;
import ai.subut.kurjun.security.service.PgpKeyFetcher;
import ai.subut.kurjun.security.service.RoleManager;
import ai.subut.kurjun.security.utils.PGPUtils;


@RunWith( MockitoJUnitRunner.class )
public class IdentityManagerImplTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static PGPPublicKey sampleKey;
    private static String signedFingerprint;

    private File file;
    private IdentityManager identityManager;
    private Identity sampleIdentity;
    private DefaultGroup group;

    @Mock
    private PgpKeyFetcher keyFetcher;

    @Mock
    private GroupManager groupManager;

    @Mock
    private RoleManager roleManager;


    @BeforeClass
    public static void setUpClass() throws IOException, PGPException
    {
        try ( InputStream is = ClassLoader.getSystemResourceAsStream( "sample.gpg.key" ) )
        {
            if ( is != null )
            {
                sampleKey = PGPUtils.readPGPKey( is );
            }
        }
        try ( InputStream is = ClassLoader.getSystemResourceAsStream( "signed.fingerprint.asc" ) )
        {
            if ( is != null )
            {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                IOUtils.copy( is, out );
                signedFingerprint = new String( out.toByteArray() );
            }
        }
    }


    @Before
    public void setUp() throws IOException
    {
        Assume.assumeNotNull( sampleKey );
        Assume.assumeNotNull( signedFingerprint );

        this.sampleIdentity = new DefaultIdentity( sampleKey );
        this.file = temporaryFolder.newFile();
        this.group = new DefaultGroup();
        this.group.setName( "some-group" );

        FileDbProvider fileDbProvider = new FileDbProviderImpl( file.getAbsolutePath() );
        Mockito.when( keyFetcher.get( sampleIdentity.getKeyFingerprint() ) ).thenReturn( sampleKey );
        Mockito.when( groupManager.getGroup( group.getName() ) ).thenReturn( group );
        this.identityManager = new IdentityManagerImpl( fileDbProvider, keyFetcher, groupManager, roleManager );

        this.identityManager.addIdentity( sampleIdentity.getKeyFingerprint(), signedFingerprint );
    }


    @After
    public void tearDown()
    {
        if ( file != null )
        {
            file.delete();
        }
    }


    @Test
    public void testGetIdentity() throws Exception
    {
        Identity id = identityManager.getIdentity( sampleIdentity.getKeyFingerprint() );
        Assert.assertEquals( sampleIdentity, id );
    }


    @Test
    public void testAddIdentity() throws Exception
    {
        Identity id = identityManager.addIdentity( sampleIdentity.getKeyFingerprint(), signedFingerprint );
        Assert.assertEquals( sampleIdentity, id );
    }


    @Test
    public void testGetGroups() throws Exception
    {
        Set<Group> groups = identityManager.getGroups( sampleIdentity );
        Assert.assertEquals( 0, groups.size() );
    }


    @Test
    public void testGetPermissions() throws Exception
    {
        Set<Permission> permissions = identityManager.getPermissions( sampleIdentity, "test" );
        Assert.assertEquals( 0, permissions.size() );
    }


    @Test
    public void testAddRemovePermissions() throws Exception
    {
        String resource = "test";
        
        Permission permission = Permission.ADD_PACKAGE;

        identityManager.addResourcePermission(permission, sampleIdentity, resource );

        Set<Permission> permissions = identityManager.getPermissions( sampleIdentity, resource );
        Assert.assertEquals( 1, permissions.size() );
        Assert.assertTrue( permissions.contains( permission ) );

        identityManager.removeResourcePermission( permission, sampleIdentity, resource );
        permissions = identityManager.getPermissions( sampleIdentity, resource );
        Assert.assertFalse( permissions.contains( permission ) );
    }

}

