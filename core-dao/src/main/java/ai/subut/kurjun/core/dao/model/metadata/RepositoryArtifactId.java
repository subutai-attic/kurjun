package ai.subut.kurjun.core.dao.model.metadata;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import ai.subut.kurjun.model.repository.ArtifactId;


/**
 *
 */
@Embeddable
public class RepositoryArtifactId implements ArtifactId,Serializable
{

    @Column (name = "md5sum")
    private String md5Sum;

    @Column (name = "context")
    private String context;

    @Column (name = "type")
    private int type;

    @Transient
    private String artifactName;

    @Transient
    private String version;

    @Transient
    private String search;


    public  RepositoryArtifactId()
    {

    }


    public  RepositoryArtifactId( String md5Sum , String context , int type )
    {
        this.md5Sum = md5Sum;
        this.context = context;
        this.type = type;
    }


    @Override
    public String getSearch()
    {
        return search;
    }


    @Override
    public void setSearch( final String search )
    {
        this.search = search;
    }


    @Override
    public String getVersion()
    {
        return version;
    }


    @Override
    public void setVersion( final String version )
    {
        this.version = version;
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
    public String getMd5Sum()
    {
        return md5Sum;
    }


    @Override
    public void setMd5Sum( final String md5Sum )
    {
        this.md5Sum = md5Sum;
    }


    @Override
    public String getArtifactName()
    {
        return artifactName;
    }


    @Override
    public void setArtifactName( final String artifactName )
    {
        this.artifactName = artifactName;
    }


    private String getUniqId()
    {
        return  context +"." + md5Sum + "." + type;
    }





    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
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
