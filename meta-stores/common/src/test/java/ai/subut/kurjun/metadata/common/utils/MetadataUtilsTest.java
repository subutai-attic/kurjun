package ai.subut.kurjun.metadata.common.utils;


import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.model.metadata.Metadata;


public class MetadataUtilsTest
{

    @Test
    public void testMakeVersionComparator()
    {
        DefaultMetadata m1 = new DefaultMetadata();
        m1.setName( "one" );
        m1.setVersion( "7.35.0" );

        DefaultMetadata m2 = new DefaultMetadata();
        m2.setName( "two" );
        m2.setVersion( null );

        DefaultMetadata m3 = new DefaultMetadata();
        m3.setName( "three" );
        m3.setVersion( "1.2-8ubuntu" );

        DefaultMetadata m4 = new DefaultMetadata();
        m4.setName( "four" );
        m4.setVersion( "4.0" );

        List<Metadata> ls = Arrays.asList( m1, m2, m3, m4 );
        ls.sort( MetadataUtils.makeVersionComparator() );

        Assert.assertEquals( m2.getName(), ls.get( 0 ).getName() );
        Assert.assertEquals( m3.getName(), ls.get( 1 ).getName() );
        Assert.assertEquals( m4.getName(), ls.get( 2 ).getName() );
        Assert.assertEquals( m1.getName(), ls.get( 3 ).getName() );
    }

}

