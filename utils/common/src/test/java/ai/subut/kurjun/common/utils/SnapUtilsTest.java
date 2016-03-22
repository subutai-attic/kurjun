package ai.subut.kurjun.common.utils;


import org.junit.Assert;
import org.junit.Test;


public class SnapUtilsTest
{


    @Test
    public void testIsValidName()
    {
        Assert.assertTrue( SnapUtils.isValidName( "snap-package" ) );
        Assert.assertTrue( SnapUtils.isValidName( "snap+knap" ) );
        Assert.assertTrue( SnapUtils.isValidName( "1-snap" ) );
        Assert.assertTrue( SnapUtils.isValidName( "1-snap+knap" ) );

        Assert.assertFalse( SnapUtils.isValidName( "snap package" ) );
        Assert.assertFalse( SnapUtils.isValidName( "_snap package" ) );
        Assert.assertFalse( SnapUtils.isValidName( " snap package" ) );
        Assert.assertFalse( SnapUtils.isValidName( "-snap package" ) );
        Assert.assertFalse( SnapUtils.isValidName( "+snap package" ) );
    }


    @Test
    public void testIsValidVersion()
    {
        Assert.assertTrue( SnapUtils.isValidVersion( "1.0.0" ) );
        Assert.assertTrue( SnapUtils.isValidVersion( "1.0.0-ubuntu" ) );
        Assert.assertTrue( SnapUtils.isValidVersion( "1.0.0-ubuntu+core" ) );
        Assert.assertTrue( SnapUtils.isValidVersion( "~1.0.0" ) );
        Assert.assertTrue( SnapUtils.isValidVersion( "beta-1" ) );
        Assert.assertTrue( SnapUtils.isValidVersion( "BETA-1" ) );

        Assert.assertFalse( SnapUtils.isValidVersion( " 1 " ) );
        Assert.assertFalse( SnapUtils.isValidVersion( "1.0_beta" ) );
        Assert.assertFalse( SnapUtils.isValidVersion( "1.0(ubuntu)" ) );
    }

}

