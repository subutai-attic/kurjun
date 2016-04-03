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
import ai.subut.kurjun.model.identity.RelationObjectType;


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
    private String uniqID;

    @Column(name = "object_type")
    private int type = RelationObjectType.User.getId();


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
    public String getUniqID()
    {
        return uniqID;
    }


    @Override
    public void setUniqID( final String uniqID )
    {
        this.uniqID = uniqID;
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
