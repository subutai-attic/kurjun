package ai.subut.kurjun.quota.disk;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.omg.CORBA.portable.InputStream;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.quota.QuotaInfoStore;
import ai.subut.kurjun.storage.factory.FileStoreFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class DiskQuotaManagerTest
{
    private DiskQuotaManager diskQuotaManager;

    @Mock
    QuotaInfoStore quotaInfoStore;

    @Mock
    KurjunContext kurjunContext;

    @Mock
    FileStoreFactory fileStoreFactory;

    @Mock
    FileStore fileStore;

    @Mock
    InputStream inputStream;


    @Before
    public void setUp() throws Exception
    {
        // mock
        when( fileStoreFactory.create( kurjunContext ) ).thenReturn( fileStore );

        diskQuotaManager = new DiskQuotaManager( quotaInfoStore, kurjunContext );

        diskQuotaManager.setFileStoreFactory( fileStoreFactory );
    }


    @Test
    public void isFull() throws Exception
    {

        diskQuotaManager.isFull();
    }


    @Test
    public void isAllowed() throws Exception
    {
        diskQuotaManager.isAllowed( 5 );
    }


    @Test
    public void getCurrentSize() throws Exception
    {
        // mock
        when( fileStore.size() ).thenReturn( Long.MAX_VALUE );

        diskQuotaManager.getCurrentSize();
    }


    @Test
    public void copyStream() throws Exception
    {
        diskQuotaManager.copyStream( inputStream );
    }
}