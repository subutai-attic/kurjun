package ai.subut.kurjun.identity;


import java.io.Serializable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.Permission;
import ai.subut.kurjun.model.identity.RelationObject;
import ai.subut.kurjun.model.identity.RelationObjectType;
import ai.subut.kurjun.model.identity.RelationType;


/**
 *
 */
public class DefaultRelation implements Relation, Serializable
{

    //*********************
    public static final String MAP_NAME = "relations";
    //*********************

    private String id;

    private RelationObject source;
    private RelationObject target;
    private RelationObject trustObject;
    private int type = RelationType.Owner.getId();

    private Set<Permission> permissions = EnumSet.noneOf( Permission.class );


    public DefaultRelation()
    {
        id  = UUID.randomUUID().toString();
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
    public String getId()
    {
        return id;
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
    public boolean equals( Object obj )
    {
        if ( obj instanceof DefaultRelation )
        {
            DefaultRelation other = ( DefaultRelation ) obj;
            return Objects.equals( this.id, other.id );
        }
        return false;
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
