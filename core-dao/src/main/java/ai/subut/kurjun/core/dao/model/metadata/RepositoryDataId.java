package ai.subut.kurjun.core.dao.model.metadata;


import java.io.Serializable;
import javax.persistence.Embeddable;


/**
 *
 */
@Embeddable
public class RepositoryDataId implements Serializable
{
    String context;
    int type;


    public RepositoryDataId()
    {

    }


    public RepositoryDataId( final String context, final int type )
    {
        this.context = context;
        this.type = type;
    }


    public String getContext()
    {
        return context;
    }


    public void setContext( final String context )
    {
        this.context = context;
    }


    public int getType()
    {
        return type;
    }


    public void setType( final int type )
    {
        this.type = type;
    }


    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( context == null ) ? 0 : context.hashCode() );
        result = prime * result + type;
        return result;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }

        RepositoryDataId other = ( RepositoryDataId ) obj;
        if ( context == null )
        {
            if ( other.context != null )
            {
                return false;
            }
        }
        else if ( !context.equals( other.context ) )
        {
            return false;
        }
        if ( type != other.type )
        {
            return false;
        }

        return true;
    }
}
