package ai.subut.kurjun.security.service;


import java.io.IOException;
import java.util.Set;

import ai.subut.kurjun.common.KurjunContext;
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


    /**
     * Gets roles of supplied identity in a given context.
     *
     * @param identity
     * @param context
     * @return
     * @throws IOException
     */
    Set<Role> getRoles( Identity identity, KurjunContext context ) throws IOException;


    /**
     * Adds role to the identity in a given context.
     *
     * @param role role to add
     * @param identity identity to add tole to
     * @param context
     * @throws IOException
     */
    void addRole( Role role, Identity identity, KurjunContext context ) throws IOException;


    /**
     * Removes role from the identity in a given context.
     *
     * @param role role to remove
     * @param identity identity to remove role from
     * @param context
     * @throws IOException
     */
    void removeRole( Role role, Identity identity, KurjunContext context ) throws IOException;


}

