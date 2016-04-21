package ai.subut.kurjun.metadata.storage.file;


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
public class DbFilePackageMetadataStoreFactoryImplTest
{
    private DbFilePackageMetadataStoreFactoryImpl storeFactory;

    @Mock
    KurjunProperties kurjunProperties;

    @Mock
    KurjunContext kurjunContext;


    @Before
    public void setUp() throws Exception
    {
        storeFactory = new DbFilePackageMetadataStoreFactoryImpl( kurjunProperties );
    }


    @Test
    public void create() throws Exception
    {
        //mock
        when( kurjunProperties.get( anyString() ) ).thenReturn( "test" );
        when( kurjunContext.getName() ).thenReturn( "test" );

        assertNotNull( storeFactory.create( kurjunContext ) );
    }
}