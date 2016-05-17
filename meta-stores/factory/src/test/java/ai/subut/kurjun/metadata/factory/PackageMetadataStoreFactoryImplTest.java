package ai.subut.kurjun.metadata.factory;


import java.security.ProviderException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.ProvisionException;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.metadata.storage.file.DbFilePackageMetadataStoreFactory;
import ai.subut.kurjun.metadata.storage.nosql.NoSqlPackageMetadataStoreFactory;
import ai.subut.kurjun.metadata.storage.sql.SqlDbPackageMetadataStoreFactory;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class PackageMetadataStoreFactoryImplTest
{
    private PackageMetadataStoreFactoryImpl storeFactory;
    private String FILE_DB = "file";
    private String SQL_DB = "sql";
    private String NOSQL_DB = "nosql";


    @Mock
    KurjunProperties kurjunProperties;

    @Mock
    KurjunContext kurjunContext;

    @Mock
    Properties properties;

    @Mock
    DbFilePackageMetadataStoreFactory dbFilePackageMetadataStoreFactory;

    @Mock
    NoSqlPackageMetadataStoreFactory noSqlPackageMetadataStoreFactory;

    @Mock
    SqlDbPackageMetadataStoreFactory sqlDbPackageMetadataStoreFactory;

    @Mock
    PackageMetadataStore packageMetadataStore;


    @Before
    public void setUp() throws Exception
    {
        storeFactory = new PackageMetadataStoreFactoryImpl( kurjunProperties, dbFilePackageMetadataStoreFactory,
                noSqlPackageMetadataStoreFactory, sqlDbPackageMetadataStoreFactory );
    }


    @Test
    public void createFileDb() throws Exception
    {
        when( kurjunProperties.getContextProperties( kurjunContext ) ).thenReturn( properties );
        when( properties.getProperty( anyString() ) ).thenReturn( FILE_DB );
        when( dbFilePackageMetadataStoreFactory.create( kurjunContext ) ).thenReturn( packageMetadataStore );

        assertNotNull( storeFactory.create( kurjunContext ) );
    }


    @Test
    public void createNoSqlDb() throws Exception
    {
        when( kurjunProperties.getContextProperties( kurjunContext ) ).thenReturn( properties );
        when( properties.getProperty( anyString() ) ).thenReturn( NOSQL_DB );
        when( noSqlPackageMetadataStoreFactory.create( kurjunContext ) ).thenReturn( packageMetadataStore );

        assertNotNull( storeFactory.create( kurjunContext ) );
    }


    @Test
    public void createSqlDb() throws Exception
    {
        when( kurjunProperties.getContextProperties( kurjunContext ) ).thenReturn( properties );
        when( properties.getProperty( anyString() ) ).thenReturn( SQL_DB );
        when( sqlDbPackageMetadataStoreFactory.create( kurjunContext ) ).thenReturn( packageMetadataStore );

        assertNotNull( storeFactory.create( kurjunContext ) );
    }


    @Test( expected = ProvisionException.class )
    public void testException()
    {
        when( kurjunProperties.getContextProperties( kurjunContext ) ).thenReturn( properties );
        when( properties.getProperty( anyString() ) ).thenReturn( "test" );


        storeFactory.create( kurjunContext );
    }
}