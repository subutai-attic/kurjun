package ai.subut.kurjun.model.identity;


import java.util.Date;

import ai.subut.kurjun.model.security.KeyAlgorithm;


/**
 *
 */
public interface User
{
    String getKeyId();

    String getKeyFingerprint();

    Date getDate();

    int getKeyLength();

    String getEmailAddress();

    String getSharedSecret();

    void setSharedSecret( String sharedSecret );

    String getSignature();

    void setSignature( String signature );
}
