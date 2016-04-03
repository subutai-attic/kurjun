package ai.subut.kurjun.core.dao.model.identity;


import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.RelationType;


/**
 *
 */
@Entity
@Table( name = RelationEntity.TABLE_NAME )
@Access( AccessType.FIELD )
public class RelationEntity implements Relation
{

    //*********************
    public static final String TABLE_NAME = "relation";
    //*********************

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id" )
    private long id;


    @Column( name = "source_object" )
    @OneToOne( cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = RelationObjectEntity.class )
    private RelationObject source;


    @Column( name = "target_object" )
    @OneToOne( cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = RelationObjectEntity.class )
    private RelationObject target;


    @Column( name = "trust_object" )
    @OneToOne( cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = RelationObjectEntity.class )
    private RelationObject trustObject;


    @Column( name = "type" )
    private int type = RelationType.Owner.getId();

    //@Enumerated( EnumType.STRING )
    //@ElementCollection(targetClass = Permission.class)
    //@CollectionTable(name = "permission", joinColumns = {@JoinColumn(name="id")})
    //@Column(name = "object_perms", nullable = false

    @Column( name = "perms" )
    private String perms = "";
    //************************


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
    public String getPerms()
    {
        return perms;
    }


    @Override
    public void setPerms( final String perms )
    {
        this.perms = perms;
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


    //**************************************
    @Override
    @Transient
    public Set<Permission> getPermissions()
    {
        if ( Strings.isNullOrEmpty( perms ) )
        {
            return Collections.emptySet();
        }
        else
        {
            Set<Permission> permSet = new HashSet<>();

            if ( perms.contains( Permission.Read.getName() ) )
                permSet.add( Permission.Read );
            if ( perms.contains( Permission.Write.getName() ) )
                permSet.add( Permission.Write );
            if ( perms.contains( Permission.Update.getName() ) )
                permSet.add( Permission.Update );
            if ( perms.contains( Permission.Delete.getName() ) )
                permSet.add( Permission.Delete );

            return permSet;
        }
    }



    @Override
    @Transient
    public void setPermissions( final Set<Permission> permissions )
    {
        perms = "";

        if ( permissions.contains( Permission.Read) )
            perms += Permission.Read.getName() +";";
        if ( permissions.contains( Permission.Write) )
            perms += Permission.Write.getName() +";";
        if ( permissions.contains( Permission.Update ))
            perms += Permission.Update.getName() +";";
        if ( permissions.contains( Permission.Delete) )
            perms += Permission.Delete.getName() +";";

    }
    //**************************************
}
