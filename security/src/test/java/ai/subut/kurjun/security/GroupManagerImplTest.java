package ai.subut.kurjun.security;


import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ai.subut.kurjun.model.security.Group;
import ai.subut.kurjun.security.service.GroupManager;


public class GroupManagerImplTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File file;
    private GroupManager groupManager;
    private DefaultGroup sampleGroup;


    @Before
    public void setUp() throws IOException
    {
        this.file = temporaryFolder.newFile();
        this.groupManager = new GroupManagerImpl( new FileDbProviderImpl( file.getAbsolutePath() ) );

        sampleGroup = new DefaultGroup();
        sampleGroup.setName( "group-name" );

        this.groupManager.addGroup( sampleGroup );
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
    public void testGetGroup() throws Exception
    {
        Group group = groupManager.getGroup( sampleGroup.getName() );
        Assert.assertEquals( sampleGroup, group );
        Assert.assertNull( groupManager.getGroup( "other-group" ) );
    }


    @Test
    public void testAddGroup() throws Exception
    {
        DefaultGroup g = new DefaultGroup();
        g.setName( "new-group" );

        Assert.assertNull( groupManager.getGroup( g.getName() ) );

        groupManager.addGroup( g );
        Assert.assertNotNull( groupManager.getGroup( g.getName() ) );
    }


    @Test
    public void testRemoveGroup() throws Exception
    {
        Assert.assertTrue( groupManager.removeGroup( sampleGroup ) );
        Assert.assertNull( groupManager.getGroup( sampleGroup.getName() ) );
        Assert.assertFalse( groupManager.removeGroup( sampleGroup ) );
    }


    @Test
    public void testAddIdentity() throws Exception
    {
        Assume.assumeTrue( "TODO: test add identity to group", false );
    }


    @Test
    public void testRemoveIdentity() throws Exception
    {
        Assume.assumeTrue( "TODO: test remove identity from group", false );
    }

}

