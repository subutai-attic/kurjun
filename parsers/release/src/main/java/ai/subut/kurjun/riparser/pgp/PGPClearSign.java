package ai.subut.kurjun.riparser.pgp;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SignatureException;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;

import org.apache.commons.io.output.NullOutputStream;


/**
 * This is a utility class to verify and decrypt clear signed documents. This is a slightly modified implementation from
 * org.bouncycastle.openpgp.examples.ClearSignedFileProcessor
 *
 */
public class PGPClearSign
{

    private PGPClearSign()
    {
    }


    /**
     * Verifies the given data input stream with provided public key stream. Optionally, data is dumped to output
     * stream.
     *
     * @param in data input stream
     * @param keyIn public key stream
     * @param outStream output stream to dump data, may be {@code null}
     *
     * @return {@code true} if given data is verified by the public key; {@code false} otherwise
     *
     * @throws IOException
     * @throws PGPException
     * @throws SignatureException
     */
    public static boolean verifyFile( InputStream in, InputStream keyIn, OutputStream outStream )
            throws IOException, PGPException, SignatureException
    {
        ArmoredInputStream aIn = new ArmoredInputStream( in );
        OutputStream dataOut = outStream != null ? outStream : NullOutputStream.NULL_OUTPUT_STREAM;
        //
        // write out signed section using the local line separator.
        // note: trailing white space needs to be removed from the end of
        // each line RFC 4880 Section 7.1
        //
        ByteArrayOutputStream lineOut = new ByteArrayOutputStream();
        int lookAhead = readInputLine( lineOut, aIn );
        byte[] lineSep = getLineSeparator();

        ByteArrayOutputStream data = new ByteArrayOutputStream();
        try ( BufferedOutputStream out = new BufferedOutputStream( data ) )
        {
            if ( lookAhead != -1 && aIn.isClearText() )
            {
                byte[] line = lineOut.toByteArray();
                int len = getLengthWithoutSeparatorOrTrailingWhitespace( line );
                out.write( line, 0, len );
                out.write( lineSep );

                dataOut.write( line, 0, len );
                dataOut.write( lineSep );

                while ( lookAhead != -1 && aIn.isClearText() )
                {
                    lookAhead = readInputLine( lineOut, lookAhead, aIn );

                    line = lineOut.toByteArray();
                    len = getLengthWithoutSeparatorOrTrailingWhitespace( line );

                    out.write( line, 0, len );
                    out.write( lineSep );

                    dataOut.write( line, 0, len );
                    dataOut.write( lineSep );
                }
            }
        }

        PGPPublicKeyRingCollection pgpRings = new PGPPublicKeyRingCollection( PGPUtil.getDecoderStream( keyIn ),
                                                                              new JcaKeyFingerprintCalculator() );

        JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory( aIn );
        PGPSignatureList p3 = ( PGPSignatureList ) pgpFact.nextObject();
        PGPSignature sig = p3.get( 0 );

        PGPPublicKey publicKey = pgpRings.getPublicKey( sig.getKeyID() );
        sig.init( new JcaPGPContentVerifierBuilderProvider().setProvider( BouncyCastleProvider.PROVIDER_NAME ),
                  publicKey );

        //
        // read the input, making sure we ignore the last newline.
        //
        try ( InputStream sigIn = new BufferedInputStream( new ByteArrayInputStream( data.toByteArray() ) ) )
        {
            lookAhead = readInputLine( lineOut, sigIn );

            processLine( sig, lineOut.toByteArray() );

            if ( lookAhead != -1 )
            {
                do
                {
                    lookAhead = readInputLine( lineOut, lookAhead, sigIn );

                    sig.update( ( byte ) '\r' );
                    sig.update( ( byte ) '\n' );

                    processLine( sig, lineOut.toByteArray() );
                }
                while ( lookAhead != -1 );
            }
        }

        return sig.verify();
    }


    private static int readInputLine( ByteArrayOutputStream bOut, InputStream fIn ) throws IOException
    {
        bOut.reset();

        int lookAhead = -1;
        int ch;
        while ( ( ch = fIn.read() ) >= 0 )
        {
            bOut.write( ch );
            if ( ch == '\r' || ch == '\n' )
            {
                lookAhead = readPassedEOL( bOut, ch, fIn );
                break;
            }
        }
        return lookAhead;
    }


    private static int readInputLine( ByteArrayOutputStream bOut, int lookAhead, InputStream fIn ) throws IOException
    {
        bOut.reset();

        int ch = lookAhead;
        do
        {
            bOut.write( ch );
            if ( ch == '\r' || ch == '\n' )
            {
                lookAhead = readPassedEOL( bOut, ch, fIn );
                break;
            }
        }
        while ( ( ch = fIn.read() ) >= 0 );

        if ( ch < 0 )
        {
            lookAhead = -1;
        }

        return lookAhead;
    }


    private static int readPassedEOL( ByteArrayOutputStream bOut, int lastCh, InputStream fIn ) throws IOException
    {
        int lookAhead = fIn.read();
        if ( lastCh == '\r' && lookAhead == '\n' )
        {
            bOut.write( lookAhead );
            lookAhead = fIn.read();
        }
        return lookAhead;
    }


    private static byte[] getLineSeparator()
    {
        String nl = System.getProperty( "line.separator" );
        byte[] nlBytes = new byte[nl.length()];
        for ( int i = 0; i != nlBytes.length; i++ )
        {
            nlBytes[i] = ( byte ) nl.charAt( i );
        }
        return nlBytes;
    }


    private static void processLine( PGPSignature sig, byte[] line ) throws SignatureException, IOException
    {
        int length = getLengthWithoutWhiteSpace( line );
        if ( length > 0 )
        {
            sig.update( line, 0, length );
        }
    }


    private static int getLengthWithoutSeparatorOrTrailingWhitespace( byte[] line )
    {
        int end = line.length - 1;
        while ( end >= 0 && isWhiteSpace( line[end] ) )
        {
            end--;
        }
        return end + 1;
    }


    private static boolean isLineEnding( byte b )
    {
        return b == '\r' || b == '\n';
    }


    private static int getLengthWithoutWhiteSpace( byte[] line )
    {
        int end = line.length - 1;

        while ( end >= 0 && isWhiteSpace( line[end] ) )
        {
            end--;
        }

        return end + 1;
    }


    private static boolean isWhiteSpace( byte b )
    {
        return isLineEnding( b ) || b == '\t' || b == ' ';
    }

}

