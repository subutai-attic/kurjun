package ai.subut.kurjun.security.service;


/**
 * Security manager for Kurjun services. This is the main class to use for authentication and authorization purposes.
 *
 */
public interface SecurityManager
{

    boolean isAuthenticated( String fingerprint );


    boolean isAllowed( String fingerprint, String itemId );
}

