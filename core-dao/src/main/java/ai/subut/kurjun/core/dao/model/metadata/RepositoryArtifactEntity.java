package ai.subut.kurjun.core.dao.model.metadata;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.model.repository.ArtifactId;
import ai.subut.kurjun.model.repository.RepositoryArtifact;


/**
 *
 */
@Entity
@Table( name = RepositoryArtifactEntity.TABLE_NAME )
@Access( AccessType.FIELD )
public class RepositoryArtifactEntity implements RepositoryArtifact
{
    public static final String TABLE_NAME = "repository_artifacts";


    @EmbeddedId
    RepositoryArtifactId id;

    @Column(name = "version")
    private String version;

    //@ManyToOne(targetEntity = RepositoryDataEntity.class)
    //private RepositoryData repositoryData;

    //@Embedded
    @Transient
    DefaultTemplate templateMetada;


    public RepositoryArtifactEntity()
    {

    }

    public RepositoryArtifactEntity(String name, String owner, String md5Sum)
    {
        id = new RepositoryArtifactId( name, owner ,md5Sum, "" ,0);
    }


    public ArtifactId getId()
    {
        return id;
    }


    public void setId( final RepositoryArtifactId id )
    {
        this.id = id;
    }


    @Override
    public String getName()
    {
        return (this.id != null)?this.id.getName():"";
    }

    @Override
    public String getOwner()
    {
        return (this.id != null)?this.id.getOwner():"";
    }


    @Override
    public String getMd5Sum()
    {
        return (this.id != null)?this.id.getMd5Sum():"";
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


    public DefaultTemplate getTemplateMetada()
    {
        return templateMetada;
    }


    public void setTemplateMetada( final DefaultTemplate templateMetada )
    {
        this.templateMetada = templateMetada;
    }


    @Override
    public void setTemplateMetada( final Object templateMetada )
    {
        this.templateMetada = (DefaultTemplate)templateMetada;
    }


}
