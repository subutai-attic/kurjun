package ai.subut.kurjun.quota;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;

import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.quota.disk.DiskQuota;
import ai.subut.kurjun.quota.transfer.TransferQuota;


/**
 * Store for quota related information. By default, store is backed by a file based db. Optionally store can be
 * initialized to be an in-memory store by setting {@link KurjunConstants#QUOTA_IN_MEMORY} property in Kurjun properties
 * file.
 * <p>
 * Quota information is saved by contexts. It is highly recommended to use different context names for different
 * repository types within an application scope.
 *
 * @see KurjunConstants#QUOTA_IN_MEMORY
 * @see QuotaManagementModule#getFileDb(ai.subut.kurjun.common.service.KurjunProperties)
 */
public class QuotaInfoStore
{
    private static final Logger LOGGER = LoggerFactory.getLogger( QuotaInfoStore.class );

    private static final String DISK_QUOTA_MAP = "disk-quota";
    private static final String TRANSFER_QUOTA_MAP = "transfer-quota";

    private Provider<FileDb> fileDbProvider;

    private Boolean inMemory = Boolean.FALSE;
    private Map<String, DiskQuota> diskQuotas;
    private Map<String, TransferQuota> transferQuotas;


    @Inject
    public QuotaInfoStore( Injector injector, KurjunProperties kurjunProperties )
    {
        this.fileDbProvider = injector.getProvider( Key.get( FileDb.class, Quota.class ) );

        this.inMemory = kurjunProperties.getBooleanWithDefault( KurjunConstants.QUOTA_IN_MEMORY, false );
        if ( inMemory )
        {
            diskQuotas = new HashMap<>();
            transferQuotas = new HashMap<>();
        }
    }


    /**
     * Gets disk quota info for the supplied context.
     *
     * @param context context for which to retrieve disk quota info
     * @return disk quota info if found; {@code null} otherwise
     * @throws IOException
     */
    public DiskQuota getDiskQuota( KurjunContext context ) throws IOException
    {
        FileDb fileDb = null;

        if ( inMemory )
        {
            return diskQuotas.get( makeKey( context ) );
        }
        try
        {
            fileDb = fileDbProvider.get();
            return fileDb.get( DISK_QUOTA_MAP, makeKey( context ), DiskQuota.class );
        }
        finally
        {
            if(fileDb != null)
                fileDb.close();
        }
    }


    /**
     * Gets transfer quota info for the supplied context.
     *
     * @param context context for which to retrieve transfer quota info
     * @return transfer quota info if found; {@code null} otherwise
     * @throws IOException
     */
    public TransferQuota getTransferQuota( KurjunContext context ) throws IOException
    {
        FileDb fileDb = null;

        if ( inMemory )
        {
            return transferQuotas.get( makeKey( context ) );
        }
        try
        {
            fileDb = fileDbProvider.get();
            return fileDb.get( TRANSFER_QUOTA_MAP, makeKey( context ), TransferQuota.class );
        }
        finally
        {
            if(fileDb != null)
                fileDb.close();
        }
    }


    /**
     * Saves the supplied disk quota info for the given context. If context had already had a value then it is
     * overwritten.
     *
     * @param diskQuota disk quota info to save
     * @param context context for which to save disk quota
     * @throws IOException
     */
    public void saveDiskQuota( DiskQuota diskQuota, KurjunContext context ) throws IOException
    {
        FileDb fileDb = null;

        if ( inMemory )
        {
            diskQuotas.put( makeKey( context ), diskQuota );
            return;
        }
        try
        {
            fileDb = fileDbProvider.get();
            fileDb.put( DISK_QUOTA_MAP, makeKey( context ), diskQuota );
        }
        finally
        {
            if(fileDb != null)
                fileDb.close();
        }
    }


    /**
     * Saves the supplied transfer quota info for the given context. If context had already had a value then it is
     * overwritten.
     *
     * @param transferQuota transfer quota info to save
     * @param context context for which to save transfer quota
     * @throws IOException
     */
    public void saveTransferQuota( TransferQuota transferQuota, KurjunContext context ) throws IOException
    {
        FileDb fileDb = null;

        if ( inMemory )
        {
            transferQuotas.put( makeKey( context ), transferQuota );
            return;
        }
        try
        {
            fileDb = fileDbProvider.get();
            fileDb.put( TRANSFER_QUOTA_MAP, makeKey( context ), transferQuota );
        }
        finally
        {
            if(fileDb != null)
                fileDb.close();
        }
    }


    /**
     * Removes disk quota info for the supplied context.
     *
     * @param context context for which to remove disk quota info
     * @throws IOException
     */
    public void removeDiskQuota( KurjunContext context ) throws IOException
    {
        FileDb fileDb = null;

        if ( inMemory )
        {
            diskQuotas.remove( makeKey( context ) );
            return;
        }
        try
        {
            fileDb = fileDbProvider.get();
            fileDb.remove( DISK_QUOTA_MAP, makeKey( context ) );
        }
        finally
        {
            if(fileDb != null)
                fileDb.close();
        }
    }


    /**
     * Removes transfer quota info for the supplied context.
     *
     * @param context context for which to remove transfer quota info
     * @throws IOException
     */
    public void removeTransferQuota( KurjunContext context ) throws IOException
    {
        FileDb fileDb = null;

        if ( inMemory )
        {
            transferQuotas.remove( makeKey( context ) );
            return;
        }
        try
        {
            fileDb = fileDbProvider.get();
            fileDb.remove( TRANSFER_QUOTA_MAP, makeKey( context ) );
        }
        finally
        {
            if(fileDb != null)
                fileDb.close();
        }
    }


    /**
     * Makes up a key for maps where quotas are saved. Separate method is added for this so that there is one place
     * where keys are made. If there will be new parameters in future to make up a key, then only this method will need
     * an update.
     *
     * @param context
     * @return
     */
    private String makeKey( KurjunContext context )
    {
        return context.getName();
    }
}

