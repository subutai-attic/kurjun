package ai.subut.kurjun.model;


import java.util.Date;


/**
 * An identity based on PGP.
 */
public interface Identity
{
    String getKeyId();
    String getKeyFingerprint();
    Date getDate();
    int getKeyLength();
    String getEmailAddress();
    KeyAlgorithm getKeyAlgorithm();
    KeyUsage[] getKeyUsages();
    boolean canUseFor( KeyUsage usage );
}
