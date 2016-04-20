package ai.subut.kurjun.storage.factory;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import junit.framework.TestCase;


@RunWith( MockitoJUnitRunner.class )
public class FileStoreModuleTest extends TestCase
{
    private FileStoreModule fileStoreFactory;


    @Before
    public void setUp() throws Exception
    {
        fileStoreFactory = new FileStoreModule();
    }

    @Test
    public void testConfigure() throws Exception
    {
    }
}