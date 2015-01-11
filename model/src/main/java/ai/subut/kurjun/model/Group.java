package ai.subut.kurjun.model;


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
