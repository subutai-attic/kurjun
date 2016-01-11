package ai.subut.kurjun.quota;


/**
 * Exception for quota related issues.
 *
 */
public class QuotaException extends Exception
{

    /**
     * Creates a new instance of <code>QuotaException</code> without detail message.
     */
    public QuotaException()
    {
    }


    /**
     * Constructs an instance of <code>QuotaException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public QuotaException( String msg )
    {
        super( msg );
    }
}

