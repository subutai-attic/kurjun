package ai.subut.kurjun.core.dao.model.identity;


import java.io.Serializable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.RelationType;


/**
 *
 */
@Entity
@Table( name = RelationObjectEntity.TABLE_NAME )
@Access( AccessType.FIELD )
public class RelationEntity implements Relation, Serializable
{

    //*********************
    public static final String TABLE_NAME = "relation";
    //*********************

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id" )
    private long id;

    @Column( name = "source_object" )
    @OneToOne( cascade = CascadeType.ALL, fetch = FetchType.EAGER , targetEntity = RelationObjectEntity.class)
    private RelationObject source;

    @Column( name = "target_object" )
    @OneToOne( cascade = CascadeType.ALL, fetch = FetchType.EAGER , targetEntity = RelationObjectEntity.class)
    private RelationObject target;

    @Column( name = "trust_object" )
    @OneToOne( cascade = CascadeType.ALL, fetch = FetchType.EAGER , targetEntity = RelationObjectEntity.class)
    private RelationObject trustObject;

    @Column( name = "type" )
    private int type = RelationType.Owner.getId();

    @Column( name = "permissions" )
    @Enumerated( EnumType.STRING )
    private Set<Permission> permissions = EnumSet.noneOf( Permission.class );



    public RelationEntity()
    {
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
    public Set<Permission> getPermissions()
    {
        return permissions;
    }


    @Override
    public void setPermissions( final Set<Permission> permissions )
    {
        this.permissions = permissions;
    }


    @Override
    public RelationObject getSource()
    {
        return source;
    }


    @Override
    public void setSource( final RelationObject source )
    {
        this.source = source;
    }


    @Override
    public RelationObject getTarget()
    {
        return target;
    }


    @Override
    public void setTarget( final RelationObject target )
    {
        this.target = target;
    }


    @Override
    public RelationObject getTrustObject()
    {
        return trustObject;
    }


    @Override
    public void setTrustObject( final RelationObject trustObject )
    {
        this.trustObject = trustObject;
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



    //*************************
    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode( this.id );
        return hash;
    }

}
