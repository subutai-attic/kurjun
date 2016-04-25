package ai.subut.kurjun.ar;


import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.TestCase.assertNotNull;


/**
 * Tests the default Tar implementation.
 */
@RunWith( MockitoJUnitRunner.class )
public class DefaultTarTest
{
    private DefaultTar defaultTar;

    @Mock
    File file;


    @Before
    public void setUp() throws Exception
    {
        defaultTar = new DefaultTar( file );
    }


    @Test
    public void testGetFile() throws IOException
    {
        assertNotNull( defaultTar.getFile() );
    }
}
