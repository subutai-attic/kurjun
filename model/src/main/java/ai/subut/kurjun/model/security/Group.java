package ai.subut.kurjun.model.security;


import java.util.Iterator;


/**
 * A group of identities.
 */
public interface Group
{
    String getName();

    Iterator<Identity> getIdentities();

    Identity getByFingerprint( String fingerprint );
}
