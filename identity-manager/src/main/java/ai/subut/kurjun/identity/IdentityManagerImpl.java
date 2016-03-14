package ai.subut.kurjun.identity;


import ai.subut.kurjun.identity.service.IdentityManager;
import com.google.inject.Inject;
import ai.subut.kurjun.security.manager.service.SecurityManager;


/**
 *
 */
public class IdentityManagerImpl implements IdentityManager
{
    @Inject
    SecurityManager securityManager;

}
