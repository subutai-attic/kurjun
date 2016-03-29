package ai.subut.kurjun.security.manager.service;


import java.io.InputStream;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;


/**
 *
 */
public interface SecurityManager
{
    /**
     * Calculates the md5 checksum of the given input stream
     *
     * @param is
     * @return md5 checksum, or <code>null</code> if exception occurred
     */
    byte[] calculateMd5( InputStream is );


    String generateUUIDRandom();


    String generateSecurePassword( String passwordToHash, String salt );


    PGPPublicKey readPGPKey( InputStream input ) throws PGPException;


    PGPPublicKey readPGPKey( String key ) throws PGPException;


    String exportPGPKeyAsASCII( PGPPublicKey key ) throws PGPException;


    String createJWToken( String headerJson, String claimJson, String sharedKey );


    boolean verifyJWTSignature( String token, String sharedKey );


    boolean verifyJWT( String token, String sharedKey );

    String getJWTSubject( String token );


    boolean verifyPGPSignature( String message, PGPPublicKeyRing pubKeyRing );


    boolean verifyPGPSignatureAndContent( String signedMessage, String content, PGPPublicKeyRing pubKeyRing );

    boolean verifyPGPSignatureAndContent( String signedMessage, String content, String pubKeyASCII );

    boolean verifyPGPSignature( String message, String pubKeyASCII );
}
