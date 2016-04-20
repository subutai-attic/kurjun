package ai.subut.kurjun.storage.s3;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class S3FileStoreModuleTest
{
    private S3FileStoreModule fileStoreModule;

    @Before
    public void setUp() throws Exception
    {
        fileStoreModule = new S3FileStoreModule();
    }


    @Test
    public void configure() throws Exception
    {
    }
}