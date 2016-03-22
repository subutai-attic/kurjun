package ai.subut.kurjun.quota.transfer;


import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import ai.subut.kurjun.quota.DataUnit;


/**
 * Transfer quota info class.
 *
 */
public class TransferQuota implements Serializable
{

    private long threshold;
    private DataUnit unit = DataUnit.MB;
    private long time;
    private TimeUnit timeUnit = TimeUnit.HOURS;


    /**
     * Transfer quota instance representing unlimited quota. Can be used in cases where quota management shall be
     * skipped.
     */
    public static final TransferQuota UNLIMITED = new TransferQuota();


    static
    {
        UNLIMITED.threshold = Long.MAX_VALUE;
        UNLIMITED.unit = DataUnit.BYTE;
        UNLIMITED.time = 1;
        UNLIMITED.timeUnit = TimeUnit.SECONDS;
    }


    /**
     * Gets threshold value for this quota. This is pure numeric value. Its unit is defined by
     * {@link TransferQuota#getUnit()}.
     *
     * @return threshold value
     */
    public long getThreshold()
    {
        return threshold;
    }


    /**
     * Sets threshold value for this quota. The value is pure numeric. Its unit is specified by
     * {@link TransferQuota#setUnit(ai.subut.kurjun.quota.DataUnit)}.
     *
     * @param threshold threshold value to set
     */
    public void setThreshold( long threshold )
    {
        this.threshold = threshold;
    }


    /**
     * Gets unit of the threshold value. Default value is {@link DataUnit#MB}.
     *
     * @return
     */
    public DataUnit getUnit()
    {
        return unit;
    }


    /**
     * Sets unit of the threshold value.
     *
     * @param unit
     */
    public void setUnit( DataUnit unit )
    {
        this.unit = unit;
    }


    /**
     * Gets time frame value during which the threshold should not exceed. This is pure numeric value. Its unit is
     * defined by {@link TransferQuota#getTimeUnit()}.
     *
     * @return time frame for threshold
     */
    public long getTime()
    {
        return time;
    }


    /**
     * Sets time frame value during which the threshold value should not exceed. The value is pure numeric. Its unit is
     * specified by {@link TransferQuota#getTimeUnit()}.
     *
     * @param time
     */
    public void setTime( long time )
    {
        this.time = time;
    }


    /**
     * Gets time unit for the time frame value of the quota. Default value is {@link TimeUnit#HOURS}.
     *
     * @return
     */
    public TimeUnit getTimeUnit()
    {
        return timeUnit;
    }


    /**
     * Sets time unit for the time frame value of the quota.
     *
     * @param timeUnit
     */
    public void setTimeUnit( TimeUnit timeUnit )
    {
        this.timeUnit = timeUnit;
    }


}

