package ai.subut.kurjun.quota.transfer;


import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;


/**
 * Transfer quota controller.
 *
 */
public class TransferQuotaController
{
    private TransferQuota quota;
    private TransferredDataCounter dataCounter;


    @Inject
    public TransferQuotaController( @Assisted TransferQuota quota, @Assisted TransferredDataCounter dataCounter )
    {
        this.quota = quota;
        this.dataCounter = dataCounter;
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
        checkTimeFrame();
        return dataCounter.get() + size < getThresholdInBytes( quota );
    }


    private void checkTimeFrame()
    {
        long timeFrameMillis = quota.getTimeUnit().toMillis( quota.getTime() );
        if ( dataCounter.getUpdatedTimestamp() + timeFrameMillis < System.currentTimeMillis() )
        {
            dataCounter.reset();
        }
    }


    private long getThresholdInBytes( TransferQuota quota )
    {
        return quota.getThreshold() * quota.getUnit().toBytes();
    }


}

