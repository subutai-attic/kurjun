package ai.subut.kurjun.security.manager;

import java.io.InputStream;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.subut.kurjun.security.manager.service.SecurityManager;
import ai.subut.kurjun.security.manager.utils.SecurityUtils;
import ai.subut.kurjun.security.manager.utils.pgp.ContentAndSignatures;
import ai.subut.kurjun.security.manager.utils.pgp.PGPEncryptionUtil;
import ai.subut.kurjun.security.manager.utils.pgp.PGPKeyUtil;
import ai.subut.kurjun.security.manager.utils.token.TokenUtils;


/**
 *
 */
public class SecurityManagerImpl implements SecurityManager
{

    private static final Logger LOGGER = LoggerFactory.getLogger( SecurityManagerImpl.class );

    /*******************************************/
    public SecurityManagerImpl()
    {

    }


    /*******************************************/
    @Override
    public byte[] calculateMd5( InputStream is )
    {
        return SecurityUtils.calculateMd5( is );
    }


    /*******************************************/
    @Override
    public String generateUUIDRandom()
    {
        return SecurityUtils.generateUUIDRandom();
    }


    /*******************************************/
    @Override
    public String generateSecurePassword( String passwordToHash, String salt )
    {
        return SecurityUtils.generateSecurePassword( passwordToHash,salt );
    }

    /********** PGP Utils ************/

    /*******************************************/
    @Override
    public PGPPublicKey readPGPKey( InputStream input ) throws PGPException
    {
        return PGPKeyUtil.readPublicKey( input );
    }


    /*******************************************/
    @Override
    public PGPPublicKey readPGPKey( String key ) throws PGPException
    {
        return PGPKeyUtil.readPublicKey ( key );
    }

    /*******************************************/
    @Override
    public String exportPGPKeyAsASCII( PGPPublicKey key ) throws PGPException
    {
        return PGPKeyUtil.exportAscii( key );
    }

    /********** JWT Utils ************/
    /*******************************************/
    @Override
    public String createJWToken(String headerJson, String claimJson,String sharedKey)
    {
        return TokenUtils.createToken( headerJson, claimJson ,sharedKey );
    }


    /*******************************************/
    @Override
    public boolean verifyJWTSignature(String token, String sharedKey)
    {
        return TokenUtils.verifySignature( token,sharedKey );
    }


    /*******************************************/
    @Override
    public boolean verifyJWT(String token, String sharedKey)
    {
        return TokenUtils.verifyToken( token,sharedKey );
    }


    /*******************************************/
    @Override
    public String getJWTSubject(String token)
    {
        return TokenUtils.getSubject( token);
    }


    /*******************************************/
    @Override
    public boolean verifyPGPSignature( String message, PGPPublicKeyRing pubKeyRing )
    {
        try
        {
            message = message.trim();

            return PGPEncryptionUtil.verifyClearSign( message.getBytes(), pubKeyRing );
        }
        catch ( Exception e )
        {
            return false;
        }
    }


    /*******************************************/
    @Override
    public boolean verifyPGPSignatureAndContent( String signedMessage,String content, PGPPublicKeyRing pubKeyRing )
    {
        try
        {
            byte[] exractedContent = PGPEncryptionUtil.extractContentFromClearSign( content.getBytes() );

            if(!content.getBytes().equals( exractedContent ))
            {
                return false;
            }

            signedMessage = signedMessage.trim();

            return PGPEncryptionUtil.verifyClearSign( signedMessage.getBytes(), pubKeyRing );
        }
        catch ( Exception e )
        {
            LOGGER.error( " ******* Error in SecurityManager" ,e );
            return false;
        }
    }


    /*******************************************/
    @Override
    public boolean verifyPGPSignatureAndContent( String signedMessage, String content, String pubKeyASCII )
    {
        try
        {
            PGPPublicKeyRing pubKeyRing = PGPKeyUtil.readPublicKeyRing( pubKeyASCII );

            byte[] exractedContent = PGPEncryptionUtil.extractContentFromClearSign( signedMessage.getBytes() );
            String extCont = new String(exractedContent);

            if(!content.toLowerCase().equals( extCont.trim().toLowerCase() ))
            {
                return false;
            }

            signedMessage = signedMessage.trim();

            return PGPEncryptionUtil.verifyClearSign( signedMessage.getBytes(), pubKeyRing );
        }
        catch ( Exception e )
        {
            LOGGER.error( " ******* Error in SecurityManager" ,e );
            return false;
        }
    }


    /*******************************************/
    @Override
    public boolean verifyPGPSignature( String message, String pubKeyASCII)
    {
        try
        {
            PGPPublicKeyRing pubKeyRing = PGPKeyUtil.readPublicKeyRing( pubKeyASCII );
            message = message.trim();

            return verifyPGPSignature( message, pubKeyRing );
        }
        catch ( PGPException e )
        {
            LOGGER.error( " ******* Error in SecurityManager" ,e );
            return false;
        }
    }

}
