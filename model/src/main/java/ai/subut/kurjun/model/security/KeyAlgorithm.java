package ai.subut.kurjun.model.security;


/**
 * The public key algorithms used.
 */
public enum KeyAlgorithm
{
    RSA( 'R' ), RSA_E( 'r' ), RSA_S( 's' ), ELGAMAL_E( 'g' ), ELGAMAL( 'G' ), DSA( 'D'), ECDSA( 'E' ), ECDH( 'e' );


    private char keyChar;


    private KeyAlgorithm( char keyChar )
    {
        this.keyChar = keyChar;
    }


    /**
     * Gets the key character associated with the key algorithm.
     * @return the key character
     */
    public char getKeyChar()
    {
        return keyChar;
    }
}
