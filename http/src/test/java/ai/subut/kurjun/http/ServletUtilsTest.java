package ai.subut.kurjun.http;


import org.junit.Assert;
import org.junit.Test;


public class ServletUtilsTest
{


    @Test
    public void testEnsureLeadingSlash()
    {
        Assert.assertEquals( "/some/path", ServletUtils.ensureLeadingSlash( "/some/path" ) );
        Assert.assertEquals( "/some/path", ServletUtils.ensureLeadingSlash( "some/path" ) );
    }


    @Test
    public void testRemoveTrailingSlash()
    {
        Assert.assertEquals( "/some/path", ServletUtils.removeTrailingSlash( "/some/path" ) );
        Assert.assertEquals( "/some/path", ServletUtils.removeTrailingSlash( "/some/path/" ) );
    }

}

