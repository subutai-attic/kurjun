package ai.subut.kurjun.metadata.storage.nosql;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class SchemaInfoTest
{
    private SchemaInfo schemaInfo;


    @Before
    public void setUp() throws Exception
    {
        schemaInfo = new SchemaInfo();
    }


    @Test
    public void getTag() throws Exception
    {
        schemaInfo.setTag( "tah" );

        assertNotNull( schemaInfo.getTag() );
    }


    @Test
    public void getTableName() throws Exception
    {
        assertEquals( "metadata", schemaInfo.getTableName() );
    }


    @Test
    public void createSchema() throws Exception
    {

    }
}