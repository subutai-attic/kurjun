package ai.subut.kurjun.identity;


import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.validator.routines.EmailValidator;

import ai.subut.kurjun.model.identity.User;



/**
 *
 */
public class DefaultUser implements User, Serializable
{

    private String keyId;
    private String keyFingerprint;
    private Date date;
    private int keyLength;
    private String emailAddress;
    private String sharedSecret;
    private String signature;


    public DefaultUser( String keyFingerprint )
    {
        this.keyFingerprint = keyFingerprint;
    }


    public DefaultUser( PGPPublicKey key )
    {
        this.keyId = String.format( "%016X", key.getKeyID() );
        this.keyFingerprint = Hex.encodeHexString( key.getFingerprint() );
        this.date = key.getCreationTime();
        this.keyLength = key.getBitStrength();
        this.emailAddress = parseEmailAddress( key );
    }


    @Override
    public String getKeyId()
    {
        return keyId;
    }


    @Override
    public String getKeyFingerprint()
    {
        return keyFingerprint;
    }


    @Override
    public Date getDate()
    {
        return date;
    }


    @Override
    public int getKeyLength()
    {
        return keyLength;
    }


    @Override
    public String getEmailAddress()
    {
        return emailAddress;
    }


    @Override
    public String getSharedSecret()
    {
        return sharedSecret;
    }


    @Override
    public void setSharedSecret( final String sharedSecret )
    {
        this.sharedSecret = sharedSecret;
    }


    @Override
    public String getSignature()
    {
        return signature;
    }


    @Override
    public void setSignature( final String signature )
    {
        this.signature = signature;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof DefaultUser )
        {
            DefaultUser other = ( DefaultUser ) obj;
            return Objects.equals( this.keyFingerprint, other.keyFingerprint );
        }
        return false;
    }


    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode( this.keyFingerprint );
        return hash;
    }


    private String parseEmailAddress( PGPPublicKey key )
    {
        Pattern uidEmailPattern = Pattern.compile( ".*?<(.*)>" );

        Iterator it = key.getUserIDs();
        while ( it.hasNext() )
        {
            Matcher matcher = uidEmailPattern.matcher( it.next().toString() );
            if ( matcher.matches() && EmailValidator.getInstance().isValid( matcher.group( 1 ) ) )
            {
                return matcher.group( 1 );
            }
        }
        return null;
    }

}

