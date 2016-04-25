package ai.subut.kurjun.quota.transfer;


import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import ai.subut.kurjun.quota.DataUnit;

import static org.junit.Assert.*;


public class TransferQuotaTest
{
    private TransferQuota transferQuota;


    @Before
    public void setUp() throws Exception
    {
        transferQuota = new TransferQuota();

        transferQuota.setUnit( DataUnit.BYTE );
        transferQuota.setThreshold( 5 );
        transferQuota.setTime( 10 );
        transferQuota.setTimeUnit( TimeUnit.HOURS );
    }


    @Test
    public void getThreshold() throws Exception
    {
        assertNotNull( transferQuota.getThreshold() );
    }


    @Test
    public void getUnit() throws Exception
    {
        assertNotNull( transferQuota.getUnit() );
    }


    @Test
    public void getTime() throws Exception
    {
        assertNotNull( transferQuota.getTime() );
    }


    @Test
    public void getTimeUnit() throws Exception
    {
        assertNotNull( transferQuota.getTimeUnit() );
    }
}