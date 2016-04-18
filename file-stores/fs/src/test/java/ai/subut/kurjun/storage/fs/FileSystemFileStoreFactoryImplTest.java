package ai.subut.kurjun.storage.fs;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ai.subut.kurjun.common.service.KurjunProperties;
import junit.framework.TestCase;


@RunWith( MockitoJUnitRunner.class )
public class FileSystemFileStoreFactoryImplTest extends TestCase
{
    private FileSystemFileStoreFactoryImpl fileStoreFactory;

    @Mock
    KurjunProperties kurjunProperties;

    @Test
    public void testCreate() throws Exception
    {
        fileStoreFactory = new FileSystemFileStoreFactoryImpl( kurjunProperties );
    }
}