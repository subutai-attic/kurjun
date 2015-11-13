package ai.subut.kurjun.http;


import java.util.List;

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


    @Test
    public void testSplitPath()
    {
        List<String> parts = ServletUtils.splitPath( "/some/cool/path" );

        Assert.assertEquals( 3, parts.size() );
        Assert.assertEquals( "some", parts.get( 0 ) );
        Assert.assertEquals( "cool", parts.get( 1 ) );
        Assert.assertEquals( "path", parts.get( 2 ) );

        // path with query string and hash part
        parts = ServletUtils.splitPath( "/some/cool/path?param=value#ref" );
        Assert.assertEquals( 3, parts.size() );
        Assert.assertEquals( "some", parts.get( 0 ) );
        Assert.assertEquals( "cool", parts.get( 1 ) );
        Assert.assertEquals( "path", parts.get( 2 ) );

        // path with query string after slash and hash part
        parts = ServletUtils.splitPath( "/some/cool/path/?param=value#ref" );
        Assert.assertEquals( 3, parts.size() );
        Assert.assertEquals( "some", parts.get( 0 ) );
        Assert.assertEquals( "cool", parts.get( 1 ) );
        Assert.assertEquals( "path", parts.get( 2 ) );

        // path with hash part
        parts = ServletUtils.splitPath( "/some/cool/path#ref" );
        Assert.assertEquals( 3, parts.size() );
        Assert.assertEquals( "some", parts.get( 0 ) );
        Assert.assertEquals( "cool", parts.get( 1 ) );
        Assert.assertEquals( "path", parts.get( 2 ) );

    }

}

