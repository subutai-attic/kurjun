package ai.subut.kurjun.metadata.common;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;


@RunWith( MockitoJUnitRunner.class )
public class MetadataListingImplTest
{
    private MetadataListingImpl metadataListing;

    @Before
    public void setUp() throws Exception
    {
        metadataListing = new MetadataListingImpl();

        metadataListing.setMarker( new Object() );
        metadataListing.setTruncated( true );
    }


    @Test
    public void getPackageMetadata() throws Exception
    {
        assertNotNull( metadataListing.getPackageMetadata() );
    }


    @Test
    public void getMarker() throws Exception
    {
        assertNotNull( metadataListing.getMarker() );
    }


    @Test
    public void isTruncated() throws Exception
    {
        assertTrue( metadataListing.isTruncated() );
    }
}