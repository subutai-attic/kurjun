package ai.subut.kurjun.metadata.storage.nosql;


import java.io.IOException;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.metadata.MetadataListing;
import ai.subut.kurjun.model.metadata.SerializableMetadata;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class NoSqlPackageMetadataStoreTest
{
    private NoSqlPackageMetadataStore metadataStore;
    private static final byte[] MD5 = { 0, 1, 2, 3, 4, 5 };


    @Mock
    CassandraSessionProvider sessionProvider;

    @Mock
    KurjunContext kurjunContext;

    @Mock
    Session session;

    @Mock
    Statement statement;

    @Mock
    ResultSet resultSet;

    @Mock
    Iterator iterator;

    @Mock
    Row row;

    @Mock
    SerializableMetadata serializableMetadata;

    @Mock
    MetadataListing metadataListing;


    @Before
    public void setUp() throws Exception
    {
        // mock
        when( session.execute( any( Statement.class ) ) ).thenReturn( resultSet );
        when( sessionProvider.get() ).thenReturn( session );
        when( kurjunContext.getName() ).thenReturn( "public" );
        when( resultSet.iterator() ).thenReturn( iterator );


        metadataStore = new NoSqlPackageMetadataStore( sessionProvider, kurjunContext );
    }


    @Test
    public void notContains() throws Exception
    {
        assertFalse( metadataStore.contains( new Object() ) );
    }


    @Test( expected = IOException.class )
    public void getException() throws Exception
    {
        // mock
        when( iterator.hasNext() ).thenReturn( true );
        when( iterator.next() ).thenReturn( row );
        when( row.getString( anyString() ) ).thenReturn( "test" );

        metadataStore.get( "test" );
    }


    @Test
    public void get() throws Exception
    {
        // mock
        when( iterator.hasNext() ).thenReturn( true ).thenReturn( false );
        when( iterator.next() ).thenReturn( row );
        when( row.getString( anyString() ) ).thenReturn( Hex.encodeHexString( MD5 ) );

        assertNotNull( metadataStore.get( "test" ) );
    }


    @Test
    public void getByIdNull() throws Exception
    {
        assertNull( metadataStore.get( new Object() ) );
    }


    @Test
    public void getById() throws Exception
    {
        // mock
        when( resultSet.one() ).thenReturn( row );
        when( row.getString( anyString() ) ).thenReturn( Hex.encodeHexString( MD5 ) );

        assertNotNull( metadataStore.get( new Object() ) );
    }


    @Test
    public void getEmptyList() throws Exception
    {
        assertTrue( metadataStore.get( null ).isEmpty() );
    }


    @Test
    public void put() throws Exception
    {
        // mock
        when( serializableMetadata.getId() ).thenReturn( row );

        assertTrue( metadataStore.put( serializableMetadata ) );
    }


    @Test
    public void remove() throws Exception
    {
        assertFalse( metadataStore.remove( serializableMetadata ) );
    }


    @Test
    public void list() throws Exception
    {
        // mock
        when( iterator.hasNext() ).thenReturn( true ).thenReturn( false );
        when( iterator.next() ).thenReturn( row );
        when( row.getString( SchemaInfo.CHECKSUM_COLUMN ) ).thenReturn( Hex.encodeHexString( MD5 ) );

        assertFalse( metadataStore.list().isTruncated() );
    }


    @Test( expected = IllegalStateException.class )
    public void listNextBatchIsNotTurnicated() throws Exception
    {
        metadataStore.listNextBatch( metadataListing );
    }


    @Test
    public void listNextBatch() throws Exception
    {
        // mock
        when( metadataListing.isTruncated() ).thenReturn( true );
        when( metadataListing.getMarker() ).thenReturn( "test" );

        assertNotNull( metadataStore.listNextBatch( metadataListing ) );
    }
}