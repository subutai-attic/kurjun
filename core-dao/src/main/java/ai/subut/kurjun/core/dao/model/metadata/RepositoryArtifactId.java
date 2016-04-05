package ai.subut.kurjun.core.dao.model.metadata;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 *
 */
@Embeddable
public class RepositoryArtifactId implements Serializable
{

    @Column (name = "name")
    String  name;

    @Column (name = "owner")
    String owner;

    @Column (name = "md5sum")
    private String md5Sum;


    public  RepositoryArtifactId()
    {

    }

    public  RepositoryArtifactId(String  name, String owner ,String md5Sum )
    {
        this.name = name;
        this.owner = owner;
        this.md5Sum = md5Sum;
    }


    public String getName()
    {
        return name;
    }


    public void setName( final String name )
    {
        this.name = name;
    }


    public String getOwner()
    {
        return owner;
    }


    public void setOwner( final String owner )
    {
        this.owner = owner;
    }


    public String getMd5Sum()
    {
        return md5Sum;
    }


    public void setMd5Sum( final String md5Sum )
    {
        this.md5Sum = md5Sum;
    }


    private String getUniqId()
    {
        return name + "." + owner+"." +md5Sum;
    }



    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        result = prime * result + ( ( owner == null ) ? 0 : owner.hashCode() );
        result = prime * result + ( ( md5Sum == null ) ? 0 : md5Sum.hashCode() );

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
