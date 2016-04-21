package ai.subut.kurjun.common.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class KurjunContextTest
{
    private KurjunContext kurjunContext;

    @Before
    public void setUp() throws Exception
    {
        kurjunContext = new KurjunContext( "public" );

        kurjunContext.setName( "public" );
    }


    @Test
    public void getName() throws Exception
    {
        assertNotNull( kurjunContext.getName() );
        KurjunConstants.FILE_STORE_FS_DIR_PATH.toString();
    }


    @Test
    public void equals() throws Exception
    {
        kurjunContext.equals( new Object() );
        kurjunContext.equals( kurjunContext );
        kurjunContext.hashCode();
        kurjunContext.toString();
    }
}