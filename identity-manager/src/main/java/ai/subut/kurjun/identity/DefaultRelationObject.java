package ai.subut.kurjun.identity;


import java.io.Serializable;
import java.util.Objects;

import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.RelationObjectType;


/**
 *
 */
public class DefaultRelationObject implements RelationObject,Serializable
{
    //*********************
    public static final String MAP_NAME = "relation-objects";
    //*********************

    private String id;
    private int type = RelationObjectType.User.getId();


    @Override
    public String getId()
    {
        return id;
    }


    @Override
    public void setId( final String id )
    {
        this.id = id;
    }

    @Override
    public int getType()
    {
        return type;
    }

    @Override
    public String getUniqId()
    {
        return id+"-"+type;
    }


    @Override
    public void setType( final int type )
    {
        this.type = type;
    }


    //*************************
    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof DefaultRelationObject )
        {
            DefaultRelationObject other = ( DefaultRelationObject ) obj;
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
