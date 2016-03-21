package ai.subut.kurjun.identity;


import java.io.Serializable;

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
    public void setType( final int type )
    {
        this.type = type;
    }

}
