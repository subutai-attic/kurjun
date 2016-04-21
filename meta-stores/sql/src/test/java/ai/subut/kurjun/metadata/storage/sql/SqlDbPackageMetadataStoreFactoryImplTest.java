package ai.subut.kurjun.metadata.storage.sql;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;

import static org.junit.Assert.*;

@RunWith( MockitoJUnitRunner.class )
public class SqlDbPackageMetadataStoreFactoryImplTest
{
    private SqlDbPackageMetadataStoreFactoryImpl storeFactory;

    @Mock
    KurjunProperties kurjunProperties;

    @Mock
    KurjunContext kurjunContext;

    @Before
    public void setUp() throws Exception
    {
        storeFactory = new SqlDbPackageMetadataStoreFactoryImpl( kurjunProperties );
    }


    @Test(expected = RuntimeException.class)
    public void create() throws Exception
    {
        storeFactory.create( kurjunContext );
    }
}