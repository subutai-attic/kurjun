package ai.subut.kurjun.metadata.storage.nosql;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class NoSqlPackageMetadataStoreModuleTest
{
    private NoSqlPackageMetadataStoreModule storeModule;

    @Before
    public void setUp() throws Exception
    {
        storeModule = new NoSqlPackageMetadataStoreModule();
    }


    @Test
    public void configure() throws Exception
    {

    }
}