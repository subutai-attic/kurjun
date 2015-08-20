package ai.subut.kurjun.db.file;


import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


@Ignore( "TODO: fails to create read only instance" )
public class FileDbReadOnlyTest
{

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private FileDbReadOnly fileDb;


    @Before
    public void setUp() throws IOException
    {
        fileDb = new FileDbReadOnly( temp.newFile().toString() );
    }


    @After
    public void tearDown() throws IOException
    {
        if ( fileDb != null )
        {
            fileDb.close();
        }
    }


    @Test( expected = UnsupportedOperationException.class )
    public void testPut()
    {
        fileDb.put( "map", "key", 22 );
    }


    @Test( expected = UnsupportedOperationException.class )
    public void testRemove()
    {
        fileDb.remove( "map", "key" );
    }

}

