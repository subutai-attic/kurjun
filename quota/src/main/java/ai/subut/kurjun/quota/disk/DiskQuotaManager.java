package ai.subut.kurjun.quota.disk;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.ProvisionException;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.quota.QuotaException;
import ai.subut.kurjun.quota.QuotaInfoStore;
import ai.subut.kurjun.storage.factory.FileStoreFactory;


/**
 * Disk quota manager.
 *
 */
public class DiskQuotaManager
{

    private static final Logger LOGGER = LoggerFactory.getLogger( DiskQuotaManager.class );

    @Inject
    private FileStoreFactory fileStoreFactory;

    private KurjunContext context;
    private DiskQuota quota;


    @Inject
    public DiskQuotaManager( QuotaInfoStore quotaInfoStore, @Assisted KurjunContext context )
    {
        this.context = context;
        try
        {
            quota = quotaInfoStore.getDiskQuota( context );
            if ( quota == null )
            {
                quota = DiskQuota.UNLIMITED;
            }
        }
        catch ( IOException ex )
        {
            throw new ProvisionException( "Failed to get disk quota to be applied.", ex );
        }

        LOGGER.info( "Disk quota manager inited for context '{}'", context );
    }


    /**
     * Checks if the underlying file store has already exceed allowed quota.
     *
     * @return {@code true} if quota is exceeded; {@code false} otherwise
     * @throws IOException
     */
    public boolean isFull() throws IOException
    {
        FileStore fileStore = fileStoreFactory.create( context );
        return fileStore.size() >= getThresholdInBytes();
    }


    /**
     * Checks if the underlying file store can accept supplied size of data without exceeding quota.
     *
     * @param size size of data to be added
     * @return {@code true} if the file store can take the given size of data; {@code false} otherwise
     * @throws IOException
     */
    public boolean isAllowed( long size ) throws IOException
    {
        FileStore fileStore = fileStoreFactory.create( context );
        return fileStore.size() + size < getThresholdInBytes();
    }


    /**
     * Gets current size of the underlying file store.
     *
     * @return size in bytes
     * @throws IOException
     */
    public long getCurrentSize() throws IOException
    {
        FileStore fileStore = fileStoreFactory.create( context );
        return fileStore.size();
    }


    /**
     * Copies the supplied stream of data simultaneously checking if the received data exceeds the disk quota. This
     * method is very useful and is central for handling disk quota management for incoming data streams. Whenever it
     * becomes known that data amount received will exceed the quota, {@link QuotaException} is thrown.
     *
     * @param is data steam to copy and check if it would exceed the quota threshold
     * @return path to temporary file where data stream is dumped
     * @throws IOException
     * @throws QuotaException if data size exceeds quota threshold
     */
    public Path copyStream( InputStream is ) throws IOException, QuotaException
    {
        long maxAllowed = getMaxAllowedSize();
        Path target = Files.createTempFile( null, null );
        try
        {
            Files.copy( new QuotaManagedStream( is, maxAllowed ), target, StandardCopyOption.REPLACE_EXISTING );
            return target;
        }
        catch ( IOException ex )
        {
            target.toFile().delete();
            if ( ex.getCause() instanceof QuotaException )
            {
                throw ( QuotaException ) ex.getCause();
            }
            throw ex;
        }
    }


    private long getThresholdInBytes()
    {
        return quota.getThreshold() * quota.getUnit().toBytes();
    }


    private long getMaxAllowedSize() throws IOException
    {
        long currentSize = getCurrentSize();
        return Math.max( getThresholdInBytes() - currentSize, 0 );
    }


}

