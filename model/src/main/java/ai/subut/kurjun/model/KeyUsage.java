package ai.subut.kurjun.model;


/**
 * The ways in which a PGP public key can be used: these may be combined.
 */
public enum KeyUsage
{
    SIG( 'S' ), CERT( 'C' ), ENC( 'E' ), AUTH( 'A' );


    /** The usage character printed out when listing keys */
    char usageChar;

    private KeyUsage( char usageChar )
    {
        this.usageChar = usageChar;
    }


    /**
     * Gets the usage character for a public PGP key.
     *
     * @return the usage character
     */
    char getUsageChar()
    {
        return usageChar;
    }
}
