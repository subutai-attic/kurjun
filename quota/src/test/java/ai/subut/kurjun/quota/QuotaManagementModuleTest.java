package ai.subut.kurjun.quota;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ai.subut.kurjun.common.service.KurjunProperties;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class QuotaManagementModuleTest
{
    private QuotaManagementModule managementModule;

    @Mock
    KurjunProperties kurjunProperties;


    @Before
    public void setUp() throws Exception
    {
        managementModule = new QuotaManagementModule();
    }


    @Test
    public void getFileDb() throws Exception
    {
        // mock
        when( kurjunProperties.getWithDefault( anyString(), anyString() ) ).thenReturn( "test" );

        assertNotNull( managementModule.getFileDb( kurjunProperties ) );
    }
}