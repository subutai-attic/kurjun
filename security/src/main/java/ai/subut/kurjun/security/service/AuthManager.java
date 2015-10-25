package ai.subut.kurjun.security.service;


import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.security.Permission;


/**
 * Authentication and authorization manager for Kurjun services. This is the main class to use for authentication and
 * authorization purposes.
 *
 */
public interface AuthManager
{

    /**
     * Authenticates the supplied fingerprint.
     *
     * @param fingerprint fingerprint to authenticate
     * @return true if authenticated; false otherwise
     */
    boolean isAuthenticated( String fingerprint );


    /**
     * Checks if supplied identity has permission on supplied context.
     *
     * @param fingerprint identity fingerprint
     * @param permission permission to check
     * @param context context for permission
     * @return
     */
    boolean isAllowed( String fingerprint, Permission permission, KurjunContext context );
}

