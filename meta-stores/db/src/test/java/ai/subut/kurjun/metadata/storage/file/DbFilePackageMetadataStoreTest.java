package ai.subut.kurjun.metadata.storage.file;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.ProvisionException;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.model.metadata.MetadataListing;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class DbFilePackageMetadataStoreTest
{
    private DbFilePackageMetadataStore metadataStore;

    @Mock
    KurjunProperties kurjunProperties;

    @Mock
    KurjunContext kurjunContext;

    @Mock
    MetadataListing metadataListing;


    @Before
    public void setUp() throws Exception
    {
        //mock
        when( kurjunProperties.get( anyString() ) ).thenReturn( "test" );
        when( kurjunContext.getName() ).thenReturn( "test" );

        metadataStore = new DbFilePackageMetadataStore( kurjunProperties, kurjunContext );
    }


    @Test( expected = ProvisionException.class )
    public void testConstructor()
    {
        //mock
        when( kurjunProperties.get( anyString() ) ).thenReturn( null );

        metadataStore = new DbFilePackageMetadataStore( kurjunProperties, kurjunContext );
    }


    @Test
    public void get() throws Exception
    {
        metadataStore.get( null );
    }


    @Test
    public void listNextBatch() throws Exception
    {
        when( metadataListing.isTruncated() ).thenReturn( true );
        when( metadataListing.getMarker() ).thenReturn( "test" );

        metadataStore.listNextBatch( metadataListing );
    }
}