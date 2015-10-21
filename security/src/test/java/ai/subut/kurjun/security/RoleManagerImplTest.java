package ai.subut.kurjun.security;


import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ai.subut.kurjun.model.security.Permission;
import ai.subut.kurjun.model.security.Role;
import ai.subut.kurjun.security.service.RoleManager;


public class RoleManagerImplTest
{

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File file;
    private RoleManager roleManager;
    private RoleImpl sampleRole;


    @Before
    public void setUp() throws IOException
    {
        this.file = temporaryFolder.newFile();
        this.roleManager = new RoleManagerImpl( new FileDbProviderImpl( file.getAbsolutePath() ) );

        sampleRole = new RoleImpl();
        sampleRole.setName( "role-name" );
        sampleRole.getPermissions().add( Permission.ADD_DEB );
        sampleRole.getPermissions().add( Permission.DEL_DEB );

        this.roleManager.addRole( sampleRole );
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
    public void testGetRole() throws Exception
    {
        Role role = roleManager.getRole( sampleRole.getName() );
        Assert.assertEquals( sampleRole, role );
        Assert.assertEquals( sampleRole.getPermissions().size(), role.getPermissions().size() );

        Assert.assertNull( roleManager.getRole( "other-role" ) );
    }


    @Test
    public void testAddRole() throws Exception
    {
        RoleImpl newRole = new RoleImpl();
        newRole.setName( "new-role" );
        newRole.getPermissions().addAll( sampleRole.getPermissions() );

        Assert.assertNull( roleManager.getRole( newRole.getName() ) );

        roleManager.addRole( newRole );
        Assert.assertNotNull( roleManager.getRole( newRole.getName() ) );
    }


    @Test
    public void testRemoveRole() throws Exception
    {
        Assert.assertTrue( roleManager.removeRole( sampleRole ) );
        Assert.assertNull( roleManager.getRole( sampleRole.getName() ) );
        Assert.assertFalse( roleManager.removeRole( sampleRole ) );
    }

}

