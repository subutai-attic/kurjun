package ai.subut.kurjun.quota.transfer;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class TransferredDataCounterTest
{
    private TransferredDataCounter dataCounter;


    @Before
    public void setUp() throws Exception
    {
        dataCounter = new TransferredDataCounter();
    }


    @Test
    public void get() throws Exception
    {
        assertNotNull( dataCounter.get() );
    }


    @Test
    public void getUpdatedTimestamp() throws Exception
    {
        assertNotNull( dataCounter.getUpdatedTimestamp() );
    }


    @Test
    public void increment() throws Exception
    {
        assertNotNull( dataCounter.increment( 5 ) );
    }


    @Test
    public void reset() throws Exception
    {
        dataCounter.reset();
    }
}