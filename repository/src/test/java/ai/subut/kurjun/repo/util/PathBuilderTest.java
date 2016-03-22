package ai.subut.kurjun.repo.util;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ai.subut.kurjun.model.index.ChecksummedResource;
import ai.subut.kurjun.model.index.IndexPackageMetaData;
import ai.subut.kurjun.model.index.ReleaseFile;


@RunWith( MockitoJUnitRunner.class )
public class PathBuilderTest
{
    @Mock
    private ReleaseFile releaseFile;

    @Mock
    private ChecksummedResource resource;

    @Mock
    private IndexPackageMetaData packageMetaData;


    @Before
    public void setUp()
    {
        Mockito.when( releaseFile.getCodename() ).thenReturn( "trusty" );
        Mockito.when( resource.getRelativePath() ).thenReturn( "main/binary-amd64/Packages" );
        Mockito.when( packageMetaData.getFilename() ).thenReturn( "pool/main/a/ark/ark-dbg_4.8.5-0ubuntu0.1_amd64.deb" );
    }


    @Test( expected = IllegalStateException.class )
    public void testSetResource()
    {
        // it is not sensible to set resource when building path for release files
        PathBuilder.instance().forReleaseIndexFile().setResource( resource );
    }


    @Test( expected = IllegalStateException.class )
    public void testSetPackageMetaData()
    {
        // it is not sensible to set package metadata when building path for release files
        PathBuilder.instance().forReleaseIndexFile().setPackageMetaData( packageMetaData );
    }


    @Test( expected = IllegalStateException.class )
    public void testBuildWithoutRelease()
    {
        PathBuilder.instance().build();
    }


    @Test( expected = IllegalStateException.class )
    public void testBuildWithoutResource()
    {
        PathBuilder.instance().setRelease( releaseFile ).build();
    }


    @Test
    public void testBuild()
    {
        String path;

        path = PathBuilder.instance().setRelease( releaseFile ).forReleaseIndexFile().build();
        Assert.assertEquals( "dists/trusty/Release", path );

        path = PathBuilder.instance().setRelease( releaseFile ).forReleaseIndexFileSigned().build();
        Assert.assertEquals( "dists/trusty/InRelease", path );

        path = PathBuilder.instance().setRelease( releaseFile ).setResource( resource ).build();
        Assert.assertEquals( "dists/trusty/main/binary-amd64/Packages", path );

        path = PathBuilder.instance().setPackageMetaData( packageMetaData ).build();
        Assert.assertEquals( "pool/main/a/ark/ark-dbg_4.8.5-0ubuntu0.1_amd64.deb", path );

    }

}

