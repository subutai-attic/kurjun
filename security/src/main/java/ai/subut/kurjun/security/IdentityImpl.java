package ai.subut.kurjun.security;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.bcpg.PublicKeyAlgorithmTags;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.validator.routines.EmailValidator;

import ai.subut.kurjun.model.security.Identity;
import ai.subut.kurjun.model.security.KeyAlgorithm;
import ai.subut.kurjun.model.security.KeyUsage;


public class IdentityImpl implements Identity
{

    private static final Pattern UID_EMAIL_PATTERN = Pattern.compile( ".*?<(.*)>" );

    private PGPPublicKey key;
    private String email;


    public IdentityImpl( byte[] keyMaterial ) throws PGPException
    {
        readPGPKeyIdentity( new ByteArrayInputStream( keyMaterial ) );
    }


    public IdentityImpl( String asciiKey ) throws PGPException
    {
        this( asciiKey.getBytes( StandardCharsets.UTF_8 ) );
    }


    @Override
    public String getKeyId()
    {
        // format long value to 16 digit hex with padding 0 if necessary
        return String.format( "%016X", key.getKeyID() );
    }


    @Override
    public String getKeyFingerprint()
    {
        return Hex.encodeHexString( key.getFingerprint() );
    }


    @Override
    public Date getDate()
    {
        return key.getCreationTime();
    }


    @Override
    public int getKeyLength()
    {
        return key.getBitStrength();
    }


    @Override
    public String getEmailAddress()
    {
        if ( email == null )
        {
            Iterator it = key.getUserIDs();
            while ( it.hasNext() )
            {
                Matcher matcher = UID_EMAIL_PATTERN.matcher( it.next().toString() );
                if ( matcher.matches() && EmailValidator.getInstance().isValid( matcher.group( 1 ) ) )
                {
                    this.email = matcher.group( 1 );
                    break;
                }
            }
        }
        return email;
    }


    @Override
    public KeyAlgorithm getKeyAlgorithm()
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


    @Override
    public KeyUsage[] getKeyUsages()
    {
        Set<KeyUsage> set = getKeyUsagesSet();
        return set.toArray( new KeyUsage[set.size()] );
    }


    @Override
    public boolean canUseFor( KeyUsage usage )
    {
        return getKeyUsagesSet().contains( usage );
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof Identity )
        {
            Identity other = ( Identity ) obj;
            return Objects.equals( this.getKeyFingerprint(), other.getKeyFingerprint() );
        }
        return false;
    }


    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode( this.getKeyFingerprint() );
        return hash;
    }


    private Set<KeyUsage> getKeyUsagesSet()
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


    private void readPGPKeyIdentity( InputStream input ) throws PGPException
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
                    this.key = pubKey;
                    return;
                }
            }
        }

        throw new IllegalArgumentException( "No key found in supplied stream" );
    }

}

