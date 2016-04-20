package ai.subut.kurjun.storage.factory;


import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.ProvisionException;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.storage.fs.FileSystemFileStoreFactory;
import ai.subut.kurjun.storage.s3.S3FileStoreFactory;
import junit.framework.TestCase;

import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class FileStoreFactoryImplTest extends TestCase
{
    private static final String CONTEXT = "public";
    private static final String FILE_SYSTEM_TYPE = "fs";
    private static final String S3_TYPE = "s3";

    private FileStoreFactoryImpl fileStoreFactory;

    @Mock
    KurjunProperties kurjunProperties;

    @Mock
    FileSystemFileStoreFactory fileSystemFileStoreFactory;

    @Mock
    S3FileStoreFactory s3FileStoreFactory;

    @Mock
    KurjunContext kurjunContext;

    @Mock
    Properties properties;

    @Mock
    FileStore fileStore;


    @Before
    public void setUp() throws Exception
    {
        fileStoreFactory = new FileStoreFactoryImpl();

        // injection
        fileStoreFactory.setProperties( kurjunProperties );
        fileStoreFactory.setFileSystemFileStoreFactory( fileSystemFileStoreFactory );
        fileStoreFactory.setS3FileStoreFactory( s3FileStoreFactory );

        // mock objects in create method
        when( kurjunContext.getName() ).thenReturn( CONTEXT );
        when( kurjunProperties.getContextProperties( CONTEXT ) ).thenReturn( properties );
    }


    @Test
    public void testCreatefileSystemFileStoreFactory() throws Exception
    {
        when( properties.getProperty( "file.storage.type" ) ).thenReturn( FILE_SYSTEM_TYPE );

        fileStoreFactory.create( kurjunContext );
    }


    @Test
    public void testCreateS3FileStoreFactory() throws Exception
    {
        when( properties.getProperty( "file.storage.type" ) ).thenReturn( S3_TYPE );

        fileStoreFactory.create( kurjunContext );
    }


    @Test( expected = ProvisionException.class )
    public void shouldThrowExceptionInvalidType() throws Exception
    {
        when( properties.getProperty( "file.storage.type" ) ).thenReturn( "test" );

        fileStoreFactory.create( kurjunContext );
    }

}