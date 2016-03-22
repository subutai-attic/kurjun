package ai.subut.kurjun.quota.disk;


import java.io.Serializable;

import ai.subut.kurjun.quota.DataUnit;


/**
 * Disk quota info class.
 *
 */
public class DiskQuota implements Serializable
{

    private long threshold;
    private DataUnit unit = DataUnit.MB;

    /**
     * Disk quota instance representing unlimited quota. Can be used in cases where quota management shall be skipped.
     */
    public static DiskQuota UNLIMITED = new DiskQuota( Long.MAX_VALUE, DataUnit.BYTE );


    public DiskQuota()
    {
    }


    public DiskQuota( long threshold, DataUnit unit )
    {
        this.threshold = threshold;
        this.unit = unit;
    }


    /**
     * Gets threshold value for this quota. This is pure numeric value. Its unit is defined by
     * {@link DiskQuota#getUnit()}.
     *
     * @return threshold value
     */
    public long getThreshold()
    {
        return threshold;
    }


    /**
     * Sets threshold value for this quota. The value is pure numeric value. Its unit is specified by
     * {@link DiskQuota#setUnit(ai.subut.kurjun.quota.DataUnit)}.
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


}

