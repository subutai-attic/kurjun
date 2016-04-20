package ai.subut.kurjun.identity;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import org.apache.commons.io.output.StringBuilderWriter;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class DefaultRelationObjectTest
{
    private static final String ID = "id";
    private DefaultRelationObject relationObject;


    @Before
    public void setUp() throws Exception
    {
        relationObject = new DefaultRelationObject();

        relationObject.setId( ID );
        relationObject.setType( 1 );
    }


    @Test
    public void getId() throws Exception
    {
        // asserts
        assertNotNull( relationObject.getId() );
    }


    @Test
    public void getType() throws Exception
    {
        // asserts
        assertNotNull( relationObject.getType() );
    }


    @Test
    public void getUniqId() throws Exception
    {
        // asserts
        assertNotNull( relationObject.getUniqId() );
    }


    @Test
    public void equals() throws Exception
    {
        relationObject.equals( new Object() );
        relationObject.equals( relationObject );
        relationObject.hashCode();
    }
}