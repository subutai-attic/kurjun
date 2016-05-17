package ai.subut.kurjun.identity;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ai.subut.kurjun.common.service.KurjunProperties;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class FileDbProviderImplTest
{
    private FileDbProviderImpl fileDbProvider;

    @Mock
    KurjunProperties kurjunProperties;


    @Before
    public void setUp() throws Exception
    {
        // mock
        when( kurjunProperties.getWithDefault( anyString(), anyString() ) ).thenReturn( "test" );

        fileDbProvider = new FileDbProviderImpl( kurjunProperties );
        fileDbProvider = new FileDbProviderImpl( "test" );
    }


    @Test
    public void get() throws Exception
    {
        assertNotNull( fileDbProvider.get() );
    }
}