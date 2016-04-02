package ai.subut.kurjun.core.dao.model.identity;


import java.io.Serializable;
import java.util.Objects;


/**
 *
 */
public class RelationObjectEntityPk implements Serializable
{
    private String id;
    private int type;


    public RelationObjectEntityPk()
    {
    }

    public RelationObjectEntityPk(String id, int type)
    {
        this.id = id;
        this.type = type;
    }


    private String getUniqId()
    {
        return id + "--" + type;
    }


    //*************************
    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof RelationObjectEntityPk )
        {
            RelationObjectEntityPk other = ( RelationObjectEntityPk ) obj;
            return Objects.equals( this.getUniqId(), other.getUniqId() );
        }
        return false;
    }


    //*************************
    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode( this.getUniqId() );
        return hash;
    }
}
