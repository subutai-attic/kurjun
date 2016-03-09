package ai.subut.kurjun.common.service;


import java.util.Objects;


/**
 * Context within a Kurjun application. Context is kind of some name space or an environment of some specific
 * configuration. For instance, user entity can have its own context where user specific properties are found.
 *
 */
public class KurjunContext
{
    private String name;


    public KurjunContext()
    {
    }


    public KurjunContext( String name )
    {
        this.name = name;
    }


    public String getName()
    {
        return name;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof KurjunContext )
        {
            return Objects.equals( name, ( ( KurjunContext ) obj ).name );
        }
        return false;
    }


    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode( this.name );
        return hash;
    }


    @Override
    public String toString()
    {
        return name;
    }


}

