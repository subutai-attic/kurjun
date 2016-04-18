package ai.subut.kurjun.storage.s3;


import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class S3FileStoreFactoryImplTest
{
    private S3FileStoreFactoryImpl fileStoreFactory;

    @Mock
    KurjunProperties kurjunProperties;

    @Mock
    KurjunContext kurjunContext;

    @Mock
    Properties properties;


    @Before
    public void setUp() throws Exception
    {
        fileStoreFactory = new S3FileStoreFactoryImpl( kurjunProperties );

        // mock
        when( kurjunProperties.get( S3FileStoreModule.S3_ACCESS_KEY ) )
                .thenReturn( AWSCredentialsProviderTest.ACCESS_KEY );
        when( kurjunProperties.get( S3FileStoreModule.S3_SECRET_KEY ) )
                .thenReturn( AWSCredentialsProviderTest.SECRET_KEY );

        when( kurjunContext.getName() ).thenReturn( "public" );
        when( kurjunProperties.getContextProperties( anyString() ) ).thenReturn( properties );
        when( properties.getProperty( S3FileStoreModule.BUCKET_NAME ) ).thenReturn( "test" );
    }


    @Test
    public void create() throws Exception
    {
        fileStoreFactory.create( kurjunContext );

        // asserts
        assertNotNull( fileStoreFactory.create( kurjunContext ) );
    }
}