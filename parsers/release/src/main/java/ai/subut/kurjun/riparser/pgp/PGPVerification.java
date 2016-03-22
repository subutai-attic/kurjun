package ai.subut.kurjun.riparser.pgp;


import java.io.IOException;
import java.io.InputStream;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PGPVerification
{
    private static final Logger LOGGER = LoggerFactory.getLogger( PGPVerification.class );


    static
    {
        Security.addProvider( new BouncyCastleProvider() );
    }


    private PGPVerification()
    {
    }


    /**
     * Verifies data in given input stream against the given detached signature stream.
     *
     * @param dataStream data input stream against which verification is done
     * @param signStream input stream of the detached signature
     * @param keyStream public key input stream
     *
     * @return <code>true</code> if verification succeeds; <code>false</code> otherwise
     *
     */
    public static boolean verifySignature( InputStream dataStream, InputStream signStream, InputStream keyStream )
    {
        try ( InputStream sis = PGPUtil.getDecoderStream( signStream ) )
        {
            JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory( sis );
            PGPSignatureList sigList;

            Object o = pgpFact.nextObject();
            if ( o instanceof PGPCompressedData )
            {
                PGPCompressedData c1 = ( PGPCompressedData ) o;
                pgpFact = new JcaPGPObjectFactory( c1.getDataStream() );
                sigList = ( PGPSignatureList ) pgpFact.nextObject();
            }
            else
            {
                sigList = ( PGPSignatureList ) o;
            }

            PGPPublicKeyRingCollection pgpPubRingCollection = new PGPPublicKeyRingCollection(
                    PGPUtil.getDecoderStream( keyStream ), new JcaKeyFingerprintCalculator() );

            PGPSignature sig = sigList.get( 0 );
            PGPPublicKey key = pgpPubRingCollection.getPublicKey( sig.getKeyID() );

            sig.init( new JcaPGPContentVerifierBuilderProvider().setProvider( BouncyCastleProvider.PROVIDER_NAME ), key );

            int ch;
            while ( ( ch = dataStream.read() ) >= 0 )
            {
                sig.update( ( byte ) ch );
            }
            return sig.verify();
        }
        catch ( IOException | PGPException ex )
        {
            LOGGER.error( "Failed to verify signature", ex );
            return false;
        }
    }
}

