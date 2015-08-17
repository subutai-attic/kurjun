package ai.subut.kurjun.db.file;


import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class FileDbTest
{
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private FileDb fileDb;
    private String map = "map";
    private String key = "key";
    private Integer value = 22;


    @Before
    public void setUp() throws IOException
    {
        fileDb = new FileDb( temp.newFile().toString() );
        fileDb.put( map, key, value );
    }


    @After
    public void tearDown() throws IOException
    {
        if ( fileDb != null )
        {
            fileDb.close();
        }
    }


    @Test
    public void testContains()
    {
        Assert.assertTrue( fileDb.contains( map, key ) );
        Assert.assertFalse( fileDb.contains( map, "other-key" ) );
    }


    @Test
    public void testGet()
    {
        Assert.assertEquals( value, fileDb.get( map, key, Integer.class ) );
        Assert.assertNull( fileDb.get( map, "other-key", Integer.class ) );
    }


    @Test
    public void testPut()
    {
        Assert.assertEquals( value, fileDb.put( map, key, 33 ) );
        Assert.assertNull( fileDb.put( map, "new-key", 44 ) );
    }


    @Test
    public void testRemove()
    {
        Assert.assertEquals( value, fileDb.remove( map, key ) );
        Assert.assertNull( fileDb.remove( map, key ) );
        Assert.assertNull( fileDb.get( map, key, Integer.class ) );
    }

}

