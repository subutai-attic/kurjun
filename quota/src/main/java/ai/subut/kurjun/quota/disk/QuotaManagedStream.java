package ai.subut.kurjun.quota.disk;


import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import ai.subut.kurjun.quota.QuotaException;


/**
 * This is an input stream wrapper that performs checks after each read operation. It checks if read bytes count do not
 * exceed specified threshold value. Whenever the threshold value is exceeded an {@link IOException} is thrown.
 * <p>
 * Exception cause should be checked by {@link IOException#getCause()} if the exception is due to threshold violation or
 * to other IO problems. The type of the causing exception will be {@link QuotaException} if the there was threshold
 * violation.
 *
 */
public class QuotaManagedStream extends FilterInputStream
{
    private final long max;
    private long total;


    public QuotaManagedStream( InputStream is, long threshold )
    {
        super( is );
        this.max = threshold;
    }


    @Override
    public int read() throws IOException
    {
        int read = super.read();
        if ( read != -1 )
        {
            total++;
            checkQuota();
        }
        return read;
    }


    @Override
    public int read( byte[] b, int off, int len ) throws IOException
    {
        int n = super.read( b, off, len );
        if ( n != -1 )
        {
            total += n;
            checkQuota();
        }
        return n;
    }


    private void checkQuota() throws IOException
    {
        if ( total > max )
        {
            throw new IOException( new QuotaException( "Data size do not fit disk quota." ) );
        }
    }


}

