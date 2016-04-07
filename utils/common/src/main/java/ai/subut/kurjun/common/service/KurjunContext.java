package ai.subut.kurjun.common.service;


import java.util.Objects;

import ai.subut.kurjun.model.identity.ObjectType;


/**
 * Context within a Kurjun application. Context is kind of some name space or an environment of some specific
 * configuration. For instance, user entity can have its own context where user specific properties are found.
 *
 */
public class KurjunContext
{
    private String name;
    private int    type;
    private String owner;


    public KurjunContext( String name , int type , String owner)
    {
        this.name = name;
        this.type = type;
        this.owner = owner;
    }

    public KurjunContext( String name)
    {
        this.name = name;
        this.type = ObjectType.TemplateRepo.getId();
    }

    public String getName()
    {
        return name;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    public String getOwner()
    {
        return owner;
    }


    public void setOwner( final String owner )
    {
        this.owner = owner;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof KurjunContext )
        {
            if(Objects.equals( name, ( ( KurjunContext ) obj ).name ) &&
                    type == ( ( KurjunContext ) obj ).type)
                return true;
            else
            {
                return false;
            }
        }
        return false;
    }


    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode( this.name + this.owner );
        return hash;
    }


    @Override
    public String toString()
    {
        return name;
    }


}

