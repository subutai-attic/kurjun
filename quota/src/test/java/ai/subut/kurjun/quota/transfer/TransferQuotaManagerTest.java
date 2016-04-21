package ai.subut.kurjun.quota.transfer;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

import com.google.inject.ProvisionException;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.quota.QuotaInfoStore;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyByte;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class TransferQuotaManagerTest
{
    private TransferQuotaManager quotaManager;

    @Mock
    QuotaInfoStore quotaInfoStore;

    @Mock
    KurjunContext kurjunContext;

    @Mock
    InputStream inputStream;

    @Mock
    OutputStream outputStream;

    @Mock
    TransferredDataCounterFactory counterFactory;

    @Mock
    TransferredDataCounter dataCounter;


    @Before
    public void setUp() throws Exception
    {
        quotaManager = new TransferQuotaManager( quotaInfoStore, kurjunContext );

        quotaManager.setDataCounterFactory( counterFactory );
    }


    @Test( expected = ProvisionException.class )
    public void testConstructorException() throws IOException
    {
        when( quotaInfoStore.getTransferQuota( kurjunContext ) ).thenThrow( IOException.class );

        quotaManager = new TransferQuotaManager( quotaInfoStore, kurjunContext );
    }


    @Test
    public void copy() throws Exception
    {
        // mock
        when( counterFactory.get( kurjunContext ) ).thenReturn( dataCounter );
        when( dataCounter.increment( anyLong() ) ).thenReturn( ( long ) -1 );
        when( inputStream.read( new byte[] { anyByte() } ) ).thenReturn( 0 ).thenReturn( -1 );

        quotaManager.copy( inputStream, outputStream );
    }


    @Test
    public void createManagedStream() throws Exception
    {
        // mock
        when( counterFactory.get( kurjunContext ) ).thenReturn( dataCounter );

        assertNotNull( quotaManager.createManagedStream( inputStream ) );
    }


    @Test
    public void allowedSizeToTransfer() throws Exception
    {
        // mock
        when( counterFactory.get( kurjunContext ) ).thenReturn( dataCounter );

        quotaManager.allowedSizeToTransfer();
    }


    @Test
    public void isAllowedToTransfer() throws Exception
    {
        // mock
        when( counterFactory.get( kurjunContext ) ).thenReturn( dataCounter );

        quotaManager.isAllowedToTransfer( 5 );
    }
}