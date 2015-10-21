package ai.subut.kurjun.security;


import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import ai.subut.kurjun.model.security.Group;
import ai.subut.kurjun.model.security.Identity;


/**
 * Default implementation of {@link Group}.
 *
 */
public class GroupImpl implements Group, Serializable
{

    private String name;
    private Set<Identity> identities;


    @Override
    public String getName()
    {
        return name;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    @Override
    public Iterator<Identity> getIdentities()
    {
        return identities.iterator();
    }


    /**
     * Adds supplied identity to this group.
     *
     * @param identity identity to add
     */
    public void addIdentity( Identity identity )
    {
        identities.add( identity );
    }


    /**
     * Removes supplied identity from this group.
     *
     * @param identity identity to remove
     */
    public void removeIdentity( Identity identity )
    {
        identities.remove( identity );
    }


    @Override
    public Identity getByFingerprint( String fingerprint )
    {
        for ( Identity id : identities )
        {
            if ( id.getKeyFingerprint().equals( fingerprint ) )
            {
                return id;
            }
        }
        return null;
    }


    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode( this.name );
        return hash;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof Group )
        {
            return Objects.equals( this.name, ( ( Group ) obj ).getName() );
        }
        return false;
    }


}

