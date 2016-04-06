package ai.subut.kurjun.core.dao.model.metadata;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import ai.subut.kurjun.model.repository.ArtifactId;


/**
 *
 */
@Embeddable
public class RepositoryArtifactId implements ArtifactId,Serializable
{

    @Column (name = "name")
    String  name;

    @Column (name = "owner")
    String owner;

    @Column (name = "md5sum")
    private String md5Sum;

    @Column (name = "context")
    private String context;

    @Column (name = "type")
    private int type;


    public  RepositoryArtifactId()
    {

    }

    @Override
    public String getName()
    {
        return name;
    }


    @Override
    public void setName( final String name )
    {
        this.name = name;
    }


    public  RepositoryArtifactId(String  name, String owner ,String md5Sum , String context , int type )
    {
        this.name = name;
        this.owner = owner;
        this.md5Sum = md5Sum;
        this.context = context;
        this.type = type;
    }


    @Override
    public String getContext()
    {
        return context;
    }


    @Override
    public void setContext( final String context )
    {
        this.context = context;
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


    @Override
    public String getOwner()
    {
        return owner;
    }


    @Override
    public void setOwner( final String owner )
    {
        this.owner = owner;
    }


    @Override
    public String getMd5Sum()
    {
        return md5Sum;
    }


    @Override
    public void setMd5Sum( final String md5Sum )
    {
        this.md5Sum = md5Sum;
    }


    private String getUniqId()
    {
        return name + "." + owner + "." + md5Sum + "." + context + "." + type;
    }



    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        result = prime * result + ( ( owner == null ) ? 0 : owner.hashCode() );
        result = prime * result + ( ( md5Sum == null ) ? 0 : md5Sum.hashCode() );
        result = prime * result + ( ( context == null ) ? 0 : context.hashCode() );
        result = prime * result + type;

        return result;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;

        RepositoryArtifactId other = ( RepositoryArtifactId ) obj;


        if ( !getUniqId().equals( other.getUniqId() ) )
        {
            return false;
        }


        return true;
    }


}
