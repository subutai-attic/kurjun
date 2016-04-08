package ai.subut.kurjun.core.dao.model.metadata;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import ai.subut.kurjun.model.metadata.RepositoryData;


/**
 *
 */
@Entity
@Table( name = RepositoryDataEntity.TABLE_NAME )
@Access( AccessType.FIELD )
public class RepositoryDataEntity implements RepositoryData
{
    public static final String TABLE_NAME = "repository";


    @EmbeddedId
    private RepositoryDataId id;


    @Column( name = "owner", nullable = false )
    private String owner;


    /*
    @OneToMany( mappedBy = "repositoryData",
                fetch = FetchType.LAZY,
                orphanRemoval = true ,
                cascade = {CascadeType.ALL},
                targetEntity = RepositoryArtifactEntity.class )
    private List<RepositoryArtifact> artifacts = new ArrayList<>();
    */

    public RepositoryDataEntity()
    {
    }


    public RepositoryDataEntity( String context, int type )
    {
        this.id = new RepositoryDataId( context, type );
    }


    public RepositoryDataId getId()
    {
        return id;
    }


    @Override
    public String getContext()
    {
        return ( this.id != null ) ? this.id.getContext() : "";
    }


    @Override
    public int getType()
    {
        return ( this.id != null ) ? this.id.getType() : 0;
    }

    @Override
    public String getTypeName()
    {
        return ( this.id != null ) ? this.id.getTypeName() : "";
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


    /*
    @Override
    public List<RepositoryArtifact> getArtifacts()
    {
        return artifacts;
    }


    @Override
    public void setArtifacts( final List<RepositoryArtifact> artifacts )
    {
        this.artifacts = artifacts;
    }*/
}
