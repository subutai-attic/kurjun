package ai.subut.kurjun.security.service;


import java.io.IOException;

import ai.subut.kurjun.model.security.Group;
import ai.subut.kurjun.model.security.Identity;


/**
 * Identity group manager.
 *
 */
public interface GroupManager
{

    /**
     * Gets group by its name.
     *
     * @param name name of the group to retrieve
     * @return group for supplied name; {@code null} if not found
     * @throws IOException
     */
    Group getGroup( String name ) throws IOException;


    /**
     * Adds group to the store.
     *
     * @param group group to add
     * @throws IOException
     */
    void addGroup( Group group ) throws IOException;


    /**
     * Removes group from the store.
     *
     * @param group group to remove
     * @return {@code true} if group exists and is removed; {@code false} otherwise
     * @throws IOException
     */
    boolean removeGroup( Group group ) throws IOException;


    /**
     * Adds identity to supplied group.
     *
     * @param identity identity to add
     * @param groupName name of the group to add identity to
     * @return group containing the supplied identity
     * @throws IOException
     */
    Group addIdentity( Identity identity, String groupName ) throws IOException;


    /**
     * Removes identity from the supplied group.
     *
     * @param identity identity to remove from group
     * @param groupName name of the group to remove identity from
     * @return group instance without supplied identity
     * @throws IOException
     */
    Group removeIdentity( Identity identity, String groupName ) throws IOException;

}

