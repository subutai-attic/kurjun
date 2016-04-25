package ai.subut.kurjun.quota;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;

import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.quota.disk.DiskQuota;
import ai.subut.kurjun.quota.transfer.TransferQuota;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class QuotaInfoStoreTest
{
    private QuotaInfoStore quotaInfoStore;

    @Mock
    KurjunProperties kurjunProperties;

    @Mock
    Injector injector;

    @Mock
    KurjunContext kurjunContext;

    @Mock
    Provider<FileDb> provider;

    @Mock
    FileDb fileDb;

    @Mock
    DiskQuota diskQuota;

    @Mock
    TransferQuota transferQuota;


    @Before
    public void setUp() throws Exception
    {
        // mock
        when( kurjunProperties.getBooleanWithDefault( KurjunConstants.QUOTA_IN_MEMORY, false ) ).thenReturn( true );
        when( injector.getProvider( Key.get( FileDb.class, Quota.class ) ) ).thenReturn( provider );

        quotaInfoStore = new QuotaInfoStore( injector, kurjunProperties );
    }


    @Test
    public void getDiskQuota() throws Exception
    {
        quotaInfoStore.getDiskQuota( kurjunContext );
    }


    @Test
    public void getDiskQuota2() throws Exception
    {
        // mock
        when( kurjunProperties.getBooleanWithDefault( KurjunConstants.QUOTA_IN_MEMORY, false ) ).thenReturn( false );
        when( provider.get() ).thenReturn( fileDb );
        when( fileDb.get( anyString(), anyString(), any() ) ).thenReturn( diskQuota );
        quotaInfoStore = new QuotaInfoStore( injector, kurjunProperties );

        assertNotNull( quotaInfoStore.getDiskQuota( kurjunContext ) );
    }


    @Test
    public void getTransferQuota() throws Exception
    {
        quotaInfoStore.getTransferQuota( kurjunContext );
    }


    @Test
    public void getTransferQuota2() throws Exception
    {
        // mock
        when( kurjunProperties.getBooleanWithDefault( KurjunConstants.QUOTA_IN_MEMORY, false ) ).thenReturn( false );
        when( provider.get() ).thenReturn( fileDb );
        when( fileDb.get( anyString(), anyString(), any() ) ).thenReturn( transferQuota );
        quotaInfoStore = new QuotaInfoStore( injector, kurjunProperties );

        assertNotNull( quotaInfoStore.getTransferQuota( kurjunContext ) );
    }


    @Test
    public void saveDiskQuota() throws Exception
    {
        quotaInfoStore.saveDiskQuota( diskQuota, kurjunContext );
    }


    @Test
    public void saveDiskQuota2() throws Exception
    {
        // mock
        when( kurjunProperties.getBooleanWithDefault( KurjunConstants.QUOTA_IN_MEMORY, false ) ).thenReturn( false );
        when( provider.get() ).thenReturn( fileDb );
        when( fileDb.put( anyString(), anyString(), any() ) ).thenReturn( diskQuota );
        quotaInfoStore = new QuotaInfoStore( injector, kurjunProperties );

        quotaInfoStore.saveDiskQuota( diskQuota, kurjunContext );
    }


    @Test
    public void saveTransferQuota() throws Exception
    {
        quotaInfoStore.saveTransferQuota( transferQuota, kurjunContext );
    }


    @Test
    public void saveTransferQuota2() throws Exception
    {
        // mock
        when( kurjunProperties.getBooleanWithDefault( KurjunConstants.QUOTA_IN_MEMORY, false ) ).thenReturn( false );
        when( provider.get() ).thenReturn( fileDb );
        when( fileDb.put( anyString(), anyString(), any() ) ).thenReturn( transferQuota );
        quotaInfoStore = new QuotaInfoStore( injector, kurjunProperties );

        quotaInfoStore.saveTransferQuota( transferQuota, kurjunContext );
    }


    @Test
    public void removeTransferQuota() throws Exception
    {
        quotaInfoStore.removeTransferQuota( kurjunContext );
    }


    @Test
    public void removeTransferQuota2() throws Exception
    {
        // mock
        when( kurjunProperties.getBooleanWithDefault( KurjunConstants.QUOTA_IN_MEMORY, false ) ).thenReturn( false );
        when( provider.get() ).thenReturn( fileDb );
        when( fileDb.remove( anyString(), any() ) ).thenReturn( transferQuota );
        quotaInfoStore = new QuotaInfoStore( injector, kurjunProperties );

        quotaInfoStore.removeTransferQuota( kurjunContext );
    }


    @Test
    public void removeDiskQuota() throws Exception
    {
        quotaInfoStore.removeDiskQuota( kurjunContext );
    }


    @Test
    public void removeDiskQuota2() throws Exception
    {
        // mock
        when( kurjunProperties.getBooleanWithDefault( KurjunConstants.QUOTA_IN_MEMORY, false ) ).thenReturn( false );
        when( provider.get() ).thenReturn( fileDb );
        when( fileDb.remove( anyString(), any() ) ).thenReturn( diskQuota );
        quotaInfoStore = new QuotaInfoStore( injector, kurjunProperties );

        quotaInfoStore.removeDiskQuota( kurjunContext );
    }
}