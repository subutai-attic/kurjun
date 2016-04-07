package ai.subut.kurjun.core.dao.model.identity;


import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.ObjectType;


/**
 *
 */
@Entity
@Table( name = RelationObjectEntity.TABLE_NAME )
@Access( AccessType.FIELD )
//@IdClass(RelationObjectEntityPk.class)
public class RelationObjectEntity implements RelationObject,Serializable
{
    //*********************
    public static final String TABLE_NAME = "relation_objects";
    //*********************

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id" )
    private long id;


    @Column(name = "object_id")
    private String objectId;


    @Column(name = "object_type")
    private int type = ObjectType.User.getId();


    @Override
    public String getObjectId()
    {
        return objectId;
    }


    @Override
    public void setObjectId( final String objectId )
    {
        this.objectId = objectId;
    }


    @Override
    public void setType( final int type )
    {
        this.type = type;
    }

    @Override
    public int getType()
    {
        return type;
    }


    @Override
    public long getId()
    {
        return id;
    }


    @Override
    public void setId( final long id )
    {
        this.id = id;
    }


    @Override
    public String getUniqueId()
    {
        return "{"+objectId + "}-{" + type + "}";
    }


}
