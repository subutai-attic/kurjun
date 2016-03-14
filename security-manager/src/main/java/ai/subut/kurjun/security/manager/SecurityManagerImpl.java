package ai.subut.kurjun.security.manager;

import java.io.InputStream;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;

import ai.subut.kurjun.security.manager.service.SecurityManager;
import ai.subut.kurjun.security.manager.utils.SecurityUtils;
import ai.subut.kurjun.security.manager.utils.pgp.PGPKeyUtil;
import ai.subut.kurjun.security.manager.utils.token.TokenUtils;


/**
 *
 */
public class SecurityManagerImpl implements SecurityManager
{
    /*******************************************/
    @Override
    public byte[] calculateMd5( InputStream is )
    {
        return SecurityUtils.calculateMd5( is );
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

}
