package ai.subut.kurjun.security.service;


/**
 * Authentication and authorization manager for Kurjun services. This is the main class to use for authentication and
 * authorization purposes.
 *
 */
public interface AuthManager
{

    boolean isAuthenticated( String fingerprint );


    boolean isAllowed( String fingerprint, String itemId );
}

