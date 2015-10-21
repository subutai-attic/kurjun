package ai.subut.kurjun.security.utils;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Iterator;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.output.NullOutputStream;


public class PGPUtils
{

    private static final Logger LOGGER = LoggerFactory.getLogger( PGPUtils.class );


    static
    {
        Security.addProvider( new BouncyCastleProvider() );
    }


    private PGPUtils()
    {
        // not to be constructed
    }


    /**
     * Reads PGP public key material from supplied input stream.
     *
     * @param input public key input stream
     * @return PGP public key
     * @throws PGPException
     */
    public static PGPPublicKey readPGPKey( InputStream input ) throws PGPException
    {
        PGPPublicKeyRingCollection pgpPub;
        try
        {
            pgpPub = new PGPPublicKeyRingCollection(
                    org.bouncycastle.openpgp.PGPUtil.getDecoderStream( input ),
                    new JcaKeyFingerprintCalculator() );
        }
        catch ( IOException | PGPException ex )
        {
            throw new PGPException( "Failed to init public key ring", ex );
        }

        Iterator keyRingIter = pgpPub.getKeyRings();
        while ( keyRingIter.hasNext() )
        {
            PGPPublicKeyRing keyRing = ( PGPPublicKeyRing ) keyRingIter.next();

            Iterator keyIter = keyRing.getPublicKeys();
            while ( keyIter.hasNext() )
            {
                PGPPublicKey pubKey = ( PGPPublicKey ) keyIter.next();
                if ( !pubKey.isRevoked() )
                {
                    return pubKey;
                }
            }
        }

        throw new IllegalArgumentException( "No key found in supplied stream" );
    }


    /**
     * Verify data in given input stream with provided public key. This method does not work with clear-signed data
     * streams.
     *
     * @param signedDataStream signed data input stream to be verified
     * @param keyStream public key input stream
     * @param out the output stream to write decrypted data, maybe {@code null} if decryption is not needed
     *
     * @return {@code true} if verification succeeds; {@code false} otherwise
     */
    public static boolean verifyData( InputStream signedDataStream, InputStream keyStream, OutputStream out )
    {
        // if output stream is not provided, ignore decrypted output
        OutputStream dataOutStream = out != null ? out : NullOutputStream.NULL_OUTPUT_STREAM;
        try ( InputStream is = PGPUtil.getDecoderStream( signedDataStream ) )
        {
            JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory( is );

            Object next = pgpFact.nextObject();
            if ( !( next instanceof PGPCompressedData ) )
            {
                LOGGER.debug( "Given signature is not a valid signature(may be a detached signature)" );
                return false;
            }
            PGPCompressedData compressedData = ( PGPCompressedData ) next;

            pgpFact = new JcaPGPObjectFactory( compressedData.getDataStream() );

            PGPOnePassSignatureList part1 = ( PGPOnePassSignatureList ) pgpFact.nextObject();
            PGPOnePassSignature ops = part1.get( 0 );

            PGPPublicKeyRingCollection pgpPubRingCollection = new PGPPublicKeyRingCollection(
                    PGPUtil.getDecoderStream( keyStream ), new JcaKeyFingerprintCalculator() );
            PGPPublicKey key = pgpPubRingCollection.getPublicKey( ops.getKeyID() );

            ops.init( new JcaPGPContentVerifierBuilderProvider().setProvider( BouncyCastleProvider.PROVIDER_NAME ), key );

            PGPLiteralData part2 = ( PGPLiteralData ) pgpFact.nextObject();
            try ( InputStream dIn = part2.getInputStream() )
            {
                int ch;
                while ( ( ch = dIn.read() ) >= 0 )
                {
                    ops.update( ( byte ) ch );
                    dataOutStream.write( ch );
                }
            }
            PGPSignatureList part3 = ( PGPSignatureList ) pgpFact.nextObject();
            return ops.verify( part3.get( 0 ) );
        }
        catch ( PGPException | IOException ex )
        {
            LOGGER.debug( "Failed to verify signed data", ex );
        }
        return false;
    }
}

