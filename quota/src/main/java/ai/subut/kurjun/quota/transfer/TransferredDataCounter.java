package ai.subut.kurjun.quota.transfer;


import java.util.concurrent.atomic.AtomicLong;


/**
 * Counter of the transferred data. This is a simple counter class that does not do quota checks. Instead it is used by
 * quota controller classes where it provides transferred amount of data and further decision is done based on that
 * value.
 *
 */
public class TransferredDataCounter
{

    private AtomicLong transferred = new AtomicLong();
    private long updatedTimestamp;


    /**
     * Constructor is package private so that instances of this class are created exclusively by a factory class
     * {@link TransferredDataCounterFactory}.
     */
    TransferredDataCounter()
    {
    }


    /**
     * Gets transferred data amount beginning from the last reset time.
     *
     * @return transferred data amount in bytes
     */
    public long get()
    {
        return transferred.get();
    }


    /**
     * Gets timestamp of the last update.
     *
     * @return
     */
    public long getUpdatedTimestamp()
    {
        return updatedTimestamp;
    }


    /**
     * Increments the transferred amount by supplied value.
     *
     * @param delta bytes amount to increment the counter
     * @return value of the counter after it is incremented
     */
    public long increment( long delta )
    {
        updatedTimestamp = System.currentTimeMillis();
        return transferred.addAndGet( delta );
    }


    /**
     * Resets the counter. Usually this is performed when time frame of the quota has reached.
     */
    public void reset()
    {
        updatedTimestamp = System.currentTimeMillis();
        transferred.set( 0 );
    }
}

