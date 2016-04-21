package ai.subut.kurjun.quota.transfer;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.quota.DataUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class TransferredDataCounterFactoryTest
{
    private TransferredDataCounterFactory counterFactory;

    @Mock
    KurjunContext kurjunContext;


    @Before
    public void setUp() throws Exception
    {
        counterFactory = new TransferredDataCounterFactory();
    }


    @Test
    public void get() throws Exception
    {
        // mock
        when( kurjunContext.getName() ).thenReturn( "test" );

        DataUnit.getByName( "test" );
        DataUnit.getByName( "byte" );

        counterFactory.get( kurjunContext );
    }
}