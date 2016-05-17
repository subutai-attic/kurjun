package ai.subut.kurjun.ar;


import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class SubutaiDebArTest
{
    private SubutaiDebAr subutaiDebAr;

    @Mock
    File file;


    @Before
    public void setUp() throws Exception
    {
        File depFile = new File( "/home/ubuntu/projects/subutai-hipi_1.0.1_amd64.deb" );
        File tmp = new File( "/home/ubuntu/projects/" );
        //        when(file.exists()).thenReturn( true );

        subutaiDebAr = new SubutaiDebAr( depFile, tmp );
    }


    @Test( expected = IllegalStateException.class )
    public void testConstructorException() throws Exception
    {
        subutaiDebAr = new SubutaiDebAr( file, file );
    }


    @Test
    public void getConfigFile() throws Exception
    {
        subutaiDebAr.getConfigFile();
    }
}