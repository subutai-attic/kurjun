package ai.subut.kurjun.quota.transfer;


import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import ai.subut.kurjun.quota.QuotaException;


/**
 * Input stream whose every read operation is propagated to transferred data counter and is checked if it does not
 * exceed the supplied threshold. When threshold value is exceeded exception is thrown.
 *
 */
class TransferQuotaManagedStream extends FilterInputStream
{

    private final TransferredDataCounter dataCounter;
    private final long max;


    /**
     * Creates a wrapper stream.
     *
     * @param is stream to wrap
     * @param dataCounter data counter that will track read amounts out of the stream
     * @param threshold allowed threshold value to read
     */
    public TransferQuotaManagedStream( InputStream is, TransferredDataCounter dataCounter, long threshold )
    {
        super( is );
        this.dataCounter = dataCounter;
        this.max = threshold;
    }


    @Override
    public int read() throws IOException
    {
        int read = super.read();
        if ( read != -1 )
        {
            incrementAndCheck( 1 );
        }
        return read;
    }


    @Override
    public int read( byte[] b, int off, int len ) throws IOException
    {
        int n = super.read( b, off, len );
        if ( n != -1 )
        {
            incrementAndCheck( n );
        }
        return n;
    }


    private void incrementAndCheck( int n ) throws IOException
    {
        long read = dataCounter.increment( n );
        if ( read > max )
        {
            throw new IOException( new QuotaException( "Transfer quota exceeded." ) );
        }
    }


}

