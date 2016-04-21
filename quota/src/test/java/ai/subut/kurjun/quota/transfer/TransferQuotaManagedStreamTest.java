package ai.subut.kurjun.quota.transfer;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.omg.CORBA.portable.InputStream;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class TransferQuotaManagedStreamTest
{
    private TransferQuotaManagedStream managedStream;

    @Mock
    InputStream inputStream;

    @Mock
    TransferredDataCounter transferredDataCounter;


    @Before
    public void setUp() throws Exception
    {
        managedStream = new TransferQuotaManagedStream( inputStream, transferredDataCounter, 5 );
    }


    @Test
    public void read() throws Exception
    {
        managedStream.read();
    }


    @Test( expected = IOException.class )
    public void readException() throws Exception
    {
        // mock
        when( transferredDataCounter.increment( anyInt() ) ).thenReturn( Long.MAX_VALUE );

        managedStream.read();
    }


    @Test
    public void read2() throws IOException
    {
        byte[] bytes = { 0, 1 };
        managedStream.read( bytes, 1, 2 );
    }
}