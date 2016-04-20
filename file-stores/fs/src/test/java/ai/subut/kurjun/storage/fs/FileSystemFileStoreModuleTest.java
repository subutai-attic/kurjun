package ai.subut.kurjun.storage.fs;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;



@RunWith( MockitoJUnitRunner.class )
public class FileSystemFileStoreModuleTest
{
    private FileSystemFileStoreModule fileStoreModule;

    @Before
    public void setUp() throws Exception
    {
        fileStoreModule = new FileSystemFileStoreModule();
    }


    @Test
    public void configure() throws Exception
    {
//        fileStoreModule.configure();
    }
}