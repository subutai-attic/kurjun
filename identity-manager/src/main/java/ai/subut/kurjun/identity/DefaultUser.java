package ai.subut.kurjun.identity;


import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.openpgp.PGPPublicKey;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.validator.routines.EmailValidator;

import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserToken;
import ai.subut.kurjun.security.manager.utils.pgp.PGPKeyUtil;


/**
 *
 */
public class DefaultUser implements User, Serializable
{

    //*********************
    public static final String MAP_NAME = "users";
    //*********************

    private String keyId = "";
    private String keyFingerprint = "";
    private Date date = null;
    private String emailAddress = "";
    private String signature = "";
    private String keyData = "";
    private int type = 2;
    private UserToken userToken = null;



    //*************************
    public DefaultUser()
    {
        this.date = new Date(System.currentTimeMillis());
        this.setUserToken( null );
        this.emailAddress = "";
    }


    public DefaultUser( PGPPublicKey key )
    {
        try
        {
            this.keyId = String.format( "%016X", key.getKeyID() );
            this.keyFingerprint = Hex.encodeHexString( key.getFingerprint() );
            this.date = key.getCreationTime();
            this.emailAddress = parseEmailAddress( key );
            this.signature = UUID.randomUUID().toString();
            this.setKeyData( PGPKeyUtil.exportAscii( key ) );
        }
        catch(Exception ex)
        {

        }
    }

    public DefaultUser( String keyASCII )
    {
        try
        {
            PGPPublicKey key = PGPKeyUtil.readPublicKey( keyASCII );
            this.keyId = String.format( "%016X", key.getKeyID() );
            this.keyFingerprint = Hex.encodeHexString( key.getFingerprint() );
            this.date = key.getCreationTime();
            this.emailAddress = parseEmailAddress( key );
            this.signature = UUID.randomUUID().toString();
            this.setKeyData( keyASCII );
        }
        catch(Exception ex)
        {

        }
    }


    //*************************
    @Override
    public void setKeyId( final String keyId )
    {
        this.keyId = keyId;
    }


    //*************************
    @Override
    public String getKeyId()
    {
        return keyId;
    }


    //*************************
    @Override
    public String getKeyFingerprint()
    {
        return keyFingerprint;
    }


    //*************************
    @Override
    public void setKeyFingerprint(String keyFingerprint)
    {
        this.keyFingerprint = keyFingerprint;
    }


    //*************************
    @Override
    public Date getDate()
    {
        return date;
    }


    //*************************
    @Override
    public String getEmailAddress()
    {
        return emailAddress;
    }


    //*************************
    @Override
    public String getSignature()
    {
        return signature;
    }


    //*************************
    @Override
    public void setSignature( final String signature )
    {
        this.signature = signature;
    }


    //*************************
    @Override
    public String getKeyData()
    {
        return keyData;
    }


    //*************************
    @Override
    public void setKeyData( final String keyData )
    {
        this.keyData = keyData;
    }


    //*************************
    @Override
    public UserToken getUserToken()
    {
        return userToken;
    }


    //*************************
    @Override
    public void setUserToken( final UserToken userToken )
    {
        this.userToken = userToken;
    }


    //*************************
    @Override
    public int getType()
    {
        return type;
    }


    //*************************
    @Override
    public void setType( final int type )
    {
        this.type = type;
    }


    //*************************
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


    //*************************
    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode( this.keyFingerprint );
        return hash;
    }


    //*************************
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

