package ai.subut.kurjun.quota;


/**
 * Digital information unit. Can be used to express data amount in disk, data transferred per time, etc.
 *
 */
public enum DataUnit
{

    /**
     * Byte.
     */
    BYTE( 1L ),
    /**
     * Kilobyte.
     */
    KB( 1L << 10 ),
    /**
     * Megabyte.
     */
    MB( 1L << 20 ),
    /**
     * Gigabyte.
     */
    GB( 1L << 30 ),
    /**
     * Terabyte.
     */
    TB( 1L << 40 );

    private long bytes;


    private DataUnit( long bytes )
    {
        this.bytes = bytes;
    }


    public long toBytes()
    {
        return bytes;
    }


    /**
     * Gets enum instance that corresponds to supplied string. String comparison is case-insensitive.
     *
     * @param s
     * @return enum that corresponds to supplied string, if there is one; {@code null} otherwise
     */
    public static DataUnit getByName( String s )
    {
        for ( DataUnit u : values() )
        {
            if ( u.toString().equalsIgnoreCase( s ) )
            {
                return u;
            }
        }
        return null;
    }


}

