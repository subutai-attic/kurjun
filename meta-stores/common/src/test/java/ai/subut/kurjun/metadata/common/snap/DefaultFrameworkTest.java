package ai.subut.kurjun.metadata.common.snap;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class DefaultFrameworkTest
{
    private DefaultFramework defaultFramework;

    @Before
    public void setUp() throws Exception
    {
        defaultFramework = new DefaultFramework();

        defaultFramework.setName( "name" );
    }


    @Test
    public void getName() throws Exception
    {
        assertNotNull( defaultFramework.getName() );
    }
}