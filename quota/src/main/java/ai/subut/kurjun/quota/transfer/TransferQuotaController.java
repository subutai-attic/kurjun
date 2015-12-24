package ai.subut.kurjun.quota.transfer;


import java.io.IOException;

import com.google.inject.Inject;
import com.google.inject.ProvisionException;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.quota.QuotaInfoStore;


/**
 * Transfer quota controller.
 *
 */
public class TransferQuotaController
{
    @Inject
    private QuotaInfoStore quotaInfoStore;

    @Inject
    private TransferredDataCounterFactory dataCounterFactory;

    private KurjunContext context;
    private TransferQuota quota;


    @Inject
    public TransferQuotaController( @Assisted KurjunContext context )
    {
        this.context = context;
        try
        {
            quota = quotaInfoStore.getTransferQuota( context );
            if ( quota == null )
            {
                quota = TransferQuota.UNLIMITED;
            }
        }
        catch ( IOException ex )
        {
            throw new ProvisionException( "Failed to get transfer quota to be applied.", ex );
        }
    }


    /**
     * Checks if the supplied data size can be transferred without exceeding quota threshold.
     *
     * @param size data size in bytes
     * @return {@code true} if transferring supplied data size would not exceed quota threshold; {@code false}
     * otherwise.
     */
    public boolean isAllowedToTransfer( long size )
    {
        TransferredDataCounter dataCounter = dataCounterFactory.get( context );
        checkTimeFrame( dataCounter );
        return dataCounter.get() + size < getThresholdInBytes( quota );
    }


    private void checkTimeFrame( TransferredDataCounter counter )
    {
        long timeFrameMillis = quota.getTimeUnit().toMillis( quota.getTime() );
        if ( counter.getUpdatedTimestamp() + timeFrameMillis < System.currentTimeMillis() )
        {
            counter.reset();
        }
    }


    private long getThresholdInBytes( TransferQuota quota )
    {
        return quota.getThreshold() * quota.getUnit().toBytes();
    }


}

