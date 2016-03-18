package ai.subut.kurjun.identity;


import java.io.Serializable;

import ai.subut.kurjun.model.identity.RelationObject;


/**
 *
 */
public class DefaultRelationObject implements RelationObject,Serializable
{
    //*********************
    public static final String MAP_NAME = "relation-objects";
    //*********************

    private String id;
    private String className;
    private int type;


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
    public String getClassName()
    {
        return className;
    }


    @Override
    public void setClassName( final String className )
    {
        this.className = className;
    }


    @Override
    public int getType()
    {
        return type;
    }


    @Override
    public void setType( final int type )
    {
        this.type = type;
    }

}
