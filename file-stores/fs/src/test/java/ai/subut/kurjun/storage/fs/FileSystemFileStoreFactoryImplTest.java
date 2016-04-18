package ai.subut.kurjun.storage.fs;


import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import junit.framework.TestCase;

import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class FileSystemFileStoreFactoryImplTest extends TestCase
{
    private static final String CONTEXT = "public";
    public static final String FILE_STORE_FS_DIR_PATH = "file.store.fs.path";

    private FileSystemFileStoreFactoryImpl fileStoreFactory;

    @Mock
    KurjunContext kurjunContext;

    @Mock
    KurjunProperties kurjunProperties;

    @Mock
    Properties properties;

    @Before
    public void setUp() throws Exception
    {
        fileStoreFactory = new FileSystemFileStoreFactoryImpl( kurjunProperties );

        // mock objects in create method
        when( kurjunContext.getName() ).thenReturn( CONTEXT );
        when( kurjunProperties.getContextProperties( kurjunContext ) ).thenReturn( properties );

    }


    @Test
    public void testCreate() throws Exception
    {
        when( properties.getProperty( FILE_STORE_FS_DIR_PATH ) ).thenReturn( "test" );

        fileStoreFactory.create( kurjunContext );

        // assert
        assertNotNull( fileStoreFactory.create( kurjunContext ) );
    }
}