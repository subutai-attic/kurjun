package ai.subut.kurjun.security.service;


import java.io.IOException;

import ai.subut.kurjun.model.security.Role;


/**
 * Role manager interface.
 *
 */
public interface RoleManager
{

    /**
     * Gets role by its name.
     *
     * @param name name of the role to retrieve
     * @return role for the supplied name; {@code null} if not found
     * @throws IOException
     */
    Role getRole( String name ) throws IOException;


    /**
     * Adds supplied role to the store.
     *
     * @param role role to add
     * @throws IOException
     */
    void addRole( Role role ) throws IOException;


    /**
     * Removes supplied role from the store. Identities having this role shall be updated.
     *
     * @param role role to remove
     * @return {@code true} if role exists and is removed; {@code false} otherwise
     * @throws IOException
     */
    boolean removeRole( Role role ) throws IOException;

}

