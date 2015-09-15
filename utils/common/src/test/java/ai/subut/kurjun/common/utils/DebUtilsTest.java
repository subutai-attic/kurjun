package ai.subut.kurjun.common.utils;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class DebUtilsTest
{


    @Before
    public void setUp()
    {
    }


    @After
    public void tearDown()
    {
    }


    @Test
    public void testIsValidVersion()
    {
        Assert.assertTrue( DebUtils.isValidVersion( "7.35.0-1ubuntu2" ) );
        Assert.assertTrue( DebUtils.isValidVersion( "1:1.2.8.dfsg-1ubuntu1" ) );

        // should start with a digit
        Assert.assertFalse( DebUtils.isValidVersion( "beta1.2.3" ) );
        // white spaces not allowed
        Assert.assertFalse( DebUtils.isValidVersion( "1.2.3+ver core" ) );
    }


    @Test
    public void testGetVersionEpoch()
    {
        Assert.assertEquals( "0", DebUtils.getVersionEpoch( "7.35.0-1ubuntu2" ) );
        Assert.assertEquals( "1", DebUtils.getVersionEpoch( "1:1.2.8.dfsg-1ubuntu1" ) );
    }


    @Test
    public void testGetVersionWithoutEpoch()
    {
        Assert.assertEquals( "7.35.0-1ubuntu2", DebUtils.getVersionWithoutEpoch( "7.35.0-1ubuntu2" ) );
        Assert.assertEquals( "1.2.8.dfsg-1ubuntu1", DebUtils.getVersionWithoutEpoch( "1:1.2.8.dfsg-1ubuntu1" ) );
    }

}

