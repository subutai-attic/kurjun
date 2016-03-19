package ai.subut.kurjun.security.manager;

import java.io.InputStream;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;

import ai.subut.kurjun.security.manager.service.SecurityManager;
import ai.subut.kurjun.security.manager.utils.SecurityUtils;
import ai.subut.kurjun.security.manager.utils.pgp.PGPEncryptionUtil;
import ai.subut.kurjun.security.manager.utils.pgp.PGPKeyUtil;
import ai.subut.kurjun.security.manager.utils.token.TokenUtils;


/**
 *
 */
public class SecurityManagerImpl implements SecurityManager
{

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
    public String getJWTSubject(String token)
    {
        return TokenUtils.getSubject( token);
    }


    /*******************************************/
    @Override
    public boolean verifyPGPSignature(String message, PGPPublicKey pubKey)
    {
        try
        {
            return PGPEncryptionUtil.verify(message.getBytes(),pubKey  );
        }
        catch ( PGPException e )
        {
            return false;
        }
    }


    /*******************************************/
    @Override
    public boolean verifyPGPSignature( String message, String pubKeyASCII)
    {
        try
        {
            PGPPublicKey pubKey = PGPKeyUtil.readPublicKey( pubKeyASCII );
            return verifyPGPSignature( message, pubKey );
        }
        catch ( PGPException e )
        {
            return false;
        }
    }

}
