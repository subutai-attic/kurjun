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
import ai.subut.kurjun.model.security.Role;
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
    private DefaultRole role;

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
        this.role = new DefaultRole();
        this.role.setName( "some-role" );

        FileDbProvider fileDbProvider = new FileDbProviderImpl( file.getAbsolutePath() );
        Mockito.when( keyFetcher.get( sampleIdentity.getKeyFingerprint() ) ).thenReturn( sampleKey );
        Mockito.when( groupManager.getGroup( group.getName() ) ).thenReturn( group );
        Mockito.when( roleManager.getRole( role.getName() ) ).thenReturn( role );
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
    public void testGetRoles() throws Exception
    {
        Set<Role> roles = identityManager.getRoles( sampleIdentity );
        Assert.assertEquals( 0, roles.size() );
    }


    @Test
    public void testAddRemoveRole() throws Exception
    {
        identityManager.addRole( role, sampleIdentity );

        Set<Role> roles = identityManager.getRoles( sampleIdentity );
        Assert.assertEquals( 1, roles.size() );
        Assert.assertTrue( roles.contains( role ) );

        identityManager.removeRole( role, sampleIdentity );
        roles = identityManager.getRoles( sampleIdentity );
        Assert.assertFalse( roles.contains( role ) );
    }

}

