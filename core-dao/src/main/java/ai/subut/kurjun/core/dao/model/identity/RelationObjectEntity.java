package ai.subut.kurjun.core.dao.model.identity;


import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.RelationObjectType;


/**
 *
 */
@Entity
@Table( name = RelationObjectEntity.TABLE_NAME )
@Access( AccessType.FIELD )
@IdClass(RelationObjectEntityPk.class)

public class RelationObjectEntity implements RelationObject,Serializable
{
    //*********************
    public static final String TABLE_NAME = "relation_objects";
    //*********************

    @Id
    @Column(name = "id")
    private String id;

    @Id
    @Column(name = "type")
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



}
