package ai.subut.kurjun.security;


import java.io.Serializable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import ai.subut.kurjun.model.security.Permission;
import ai.subut.kurjun.model.security.Role;


/**
 * Default {@link Role} implementation.
 *
 */
public class RoleImpl implements Role, Serializable
{

    private String name;
    private Set<Permission> permissions = EnumSet.noneOf( Permission.class );


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
    public Set<Permission> getPermissions()
    {
        return permissions;
    }


    @Override
    public boolean hasPermission( Permission perm )
    {
        return permissions.contains( perm );
    }


    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode( this.name );
        return hash;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof Role )
        {
            return Objects.equals( this.name, ( ( Role ) obj ).getName() );
        }
        return false;
    }


}

