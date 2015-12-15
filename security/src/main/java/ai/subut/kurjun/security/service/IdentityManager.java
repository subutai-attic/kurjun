package ai.subut.kurjun.security.service;


import java.io.IOException;
import java.util.Set;

import ai.subut.kurjun.model.security.Group;
import ai.subut.kurjun.model.security.Identity;
import ai.subut.kurjun.model.security.Permission;


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
     * Adds identity with supplied fingerprint.
     *
     * @param fingerprint fingerprint of the PGP key which is used as identity
     * @param checkKeyExistence flag to check the existence of PGP key from the configured key server for the given fingerprint
     * @return identity instance if key for supplied fingerprint was found; {@code null} otherwise
     * @throws IOException
     */
    Identity addIdentity( String fingerprint, boolean checkKeyExistence ) throws IOException;


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
     * Gets permissions of supplied identity for the given resource.
     *
     * @param identity
     * @param resource
     * @return
     * @throws IOException
     */
    Set<Permission> getPermissions( Identity identity, String resource ) throws IOException;


    /**
     * Adds permission to the identity for the given resource.
     *
     * @param permission permission to add
     * @param identity identity to add permission to
     * @param resource
     * @throws IOException
     */
    void addResourcePermission( Permission permission, Identity identity, String resource ) throws IOException;


    /**
     * Removes permission from the identity for a given resource.
     *
     * @param permission permission to remove
     * @param identity identity to remove permission from
     * @param resource
     * @throws IOException
     */
    void removeResourcePermission( Permission permission, Identity identity, String resource ) throws IOException;


}

