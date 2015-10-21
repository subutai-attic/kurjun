package ai.subut.kurjun.security.service;


import java.io.IOException;
import java.util.Set;

import ai.subut.kurjun.model.security.Group;
import ai.subut.kurjun.model.security.Identity;
import ai.subut.kurjun.model.security.Role;


/**
 * Identity manager for Kurjun services.
 *
 */
public interface IdentityManager
{

    /**
     * Gets identity for supplied fingerprint. If no identity is found, supplied identity shall be considered as not
     * authenticated to use Kurjun services.
     *
     * @param fingerprint fingerprint of the identity
     * @return identity; {@code null} if not found
     * @throws IOException
     */
    Identity getIdentity( String fingerprint ) throws IOException;


    /**
     * Adds identity to the store. To ensure the identity this method expects fingerprint and signed form of that
     * identity so that we can be sure that identity is provided by its real owner.
     *
     * @param fingerprint fingerprint of the identity to add
     * @param signedFingerprint ASCII-armored signed form of the fingerprint
     * @return identity added to the store
     * @throws IOException
     */
    Identity addIdentity( String fingerprint, String signedFingerprint ) throws IOException;


    /**
     * Gets groups where supplied identity belongs to.
     *
     * @param identity identity whose groups will be fetched
     * @return groups where supplied identity belongs to
     * @throws IOException
     */
    Set<Group> getGroups( Identity identity ) throws IOException;


    Set<Role> getRoles( Identity identity ) throws IOException;


    void addRole( Role role, Identity identity ) throws IOException;


    void removeRole( Role role, Identity identity ) throws IOException;


}

