package ai.subut.kurjun.model.security;


import java.util.Set;


/**
 * A Kurjun Role
 */
public interface Role
{
    String getName();

    Set<Permission> getPermissions();

    boolean hasPermission( Permission perm );
}
