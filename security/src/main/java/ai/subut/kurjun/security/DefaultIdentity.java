package ai.subut.kurjun.security;


import java.io.Serializable;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.bcpg.PublicKeyAlgorithmTags;
import org.bouncycastle.openpgp.PGPPublicKey;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.validator.routines.EmailValidator;

import ai.subut.kurjun.model.security.Identity;
import ai.subut.kurjun.model.security.KeyAlgorithm;
import ai.subut.kurjun.model.security.KeyUsage;


public class DefaultIdentity implements Identity, Serializable
{
    private String keyId;
    private String keyFingerprint;
    private Date date;
    private int keyLength;
    private String emailAddress;
    private KeyAlgorithm keyAlgorithm;
    private Set<KeyUsage> keyUsages = EnumSet.noneOf( KeyUsage.class );


    public DefaultIdentity()
    {
    }


    public DefaultIdentity( String keyFingerprint )
    {
        this.keyFingerprint = keyFingerprint;
    }


    public DefaultIdentity( PGPPublicKey key )
    {
        this.keyId = String.format( "%016X", key.getKeyID() );
        this.keyFingerprint = Hex.encodeHexString( key.getFingerprint() );
        this.date = key.getCreationTime();
        this.keyLength = key.getBitStrength();
        this.emailAddress = parseEmailAddress( key );
        this.keyAlgorithm = retrieveKeyAlgorithm( key );
        this.keyUsages.addAll( getKeyUsagesSet( key ) );
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
    public KeyAlgorithm getKeyAlgorithm()
    {
        return keyAlgorithm;
    }


    @Override
    public KeyUsage[] getKeyUsages()
    {
        return keyUsages.toArray( new KeyUsage[keyUsages.size()] );
    }


    @Override
    public boolean canUseFor( KeyUsage usage )
    {
        return keyUsages.contains( usage );
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof DefaultIdentity )
        {
            DefaultIdentity other = ( DefaultIdentity ) obj;
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


    private KeyAlgorithm retrieveKeyAlgorithm( PGPPublicKey key )
    {
        switch ( key.getAlgorithm() )
        {
            case PublicKeyAlgorithmTags.RSA_GENERAL:
                return KeyAlgorithm.RSA;

            case PublicKeyAlgorithmTags.RSA_ENCRYPT:
                return KeyAlgorithm.RSA_E;

            case PublicKeyAlgorithmTags.RSA_SIGN:
                return KeyAlgorithm.RSA_S;

            case PublicKeyAlgorithmTags.ELGAMAL_GENERAL:
                return KeyAlgorithm.ELGAMAL;

            case PublicKeyAlgorithmTags.ELGAMAL_ENCRYPT:
                return KeyAlgorithm.ELGAMAL_E;

            case PublicKeyAlgorithmTags.DSA:
                return KeyAlgorithm.DSA;

            case PublicKeyAlgorithmTags.ECDSA:
                return KeyAlgorithm.ECDSA;

            case PublicKeyAlgorithmTags.ECDH:
                return KeyAlgorithm.ECDH;

            default:
                return null;
        }
    }


    private Set<KeyUsage> getKeyUsagesSet( PGPPublicKey key )
    {
        EnumSet<KeyUsage> set = EnumSet.noneOf( KeyUsage.class );
        if ( key.isEncryptionKey() )
        {
            set.add( KeyUsage.ENC );
        }

        // TODO: check for other usages
        // related topic here but have to be checked http://stackoverflow.com/questions/26554918/

        return set;
    }


}

