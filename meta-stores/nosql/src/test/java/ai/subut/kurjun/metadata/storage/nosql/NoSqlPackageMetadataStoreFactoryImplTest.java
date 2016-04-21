package ai.subut.kurjun.metadata.storage.nosql;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.datastax.driver.core.Session;

import ai.subut.kurjun.common.service.KurjunContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class NoSqlPackageMetadataStoreFactoryImplTest
{
    private NoSqlPackageMetadataStoreFactoryImpl storeFactory;

    @Mock
    CassandraSessionProvider sessionProvider;

    @Mock
    KurjunContext kurjunContext;

    @Mock
    Session session;


    @Before
    public void setUp() throws Exception
    {
        storeFactory = new NoSqlPackageMetadataStoreFactoryImpl( sessionProvider );
    }


    @Test
    public void create() throws Exception
    {
        // mock
        when( sessionProvider.get() ).thenReturn( session );
        when( kurjunContext.getName() ).thenReturn( "public" );

        assertNotNull( storeFactory.create( kurjunContext ) );
    }
}