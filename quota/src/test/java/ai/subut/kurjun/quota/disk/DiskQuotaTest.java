package ai.subut.kurjun.quota.disk;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import ai.subut.kurjun.quota.DataUnit;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class DiskQuotaTest
{
    private DiskQuota diskQuota;


    @Before
    public void setUp() throws Exception
    {
        diskQuota = new DiskQuota( 5, DataUnit.BYTE );
        diskQuota = new DiskQuota();

        diskQuota.setThreshold( 5 );
        diskQuota.setUnit( DataUnit.BYTE );
    }


    @Test
    public void getThreshold() throws Exception
    {
        assertNotNull( diskQuota.getThreshold() );
    }


    @Test
    public void getUnit() throws Exception
    {
        assertNotNull( diskQuota.getUnit() );
    }
}