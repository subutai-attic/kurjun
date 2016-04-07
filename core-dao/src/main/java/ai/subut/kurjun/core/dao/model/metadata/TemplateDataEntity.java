package ai.subut.kurjun.core.dao.model.metadata;


import java.util.HashMap;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.template.TemplateData;
import ai.subut.kurjun.model.repository.ArtifactId;


@Entity
@Table( name = TemplateDataEntity.TABLE_NAME )
@Access( AccessType.FIELD )
public class TemplateDataEntity implements TemplateData
{
    public static final String TABLE_NAME = "template_data";

    @EmbeddedId
    RepositoryArtifactId id;

    @Column (name = "owner" , nullable = false)
    String owner;

    @Column (name = "name", nullable = false)
    String  name;

    @Column( name = "version" )
    private String version;

    @Column( name = "parent" )
    private String parent;

    @Column( name = "package_name" )
    private String packageName;

    @Enumerated( EnumType.STRING )
    @Column( name = "architecture" )
    private Architecture architecture;

    @Lob
    @Column( name = "config_contents" )
    private String configContents;

    @Lob
    @Column( name = "packages_contents" )
    private String packagesContents;

    @Column( name = "size" )
    private long size = 0;


    @MapKeyColumn
    @ElementCollection( fetch = FetchType.LAZY )
    @Column( name = "extra" )
    private Map<String, String> extra = new HashMap<>();


    public TemplateDataEntity()
    {

    }

    public TemplateDataEntity( String md5Sum, String context , int type)
    {
        id = new RepositoryArtifactId( md5Sum , context , type );
    }


    @Override
    public String getOwner()
    {
        return owner;
    }


    @Override
    public void setOwner( String owner )
    {
        this.owner = owner;
    }

    @Override
    public String getUniqId()
    {
        return ( this.id != null ) ? this.id.getContext() + "." + this.id.getMd5Sum() : "";
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
    public String getMd5Sum()
    {
        return ( this.id != null ) ? this.id.getMd5Sum() : "";
    }


    @Override
    public Object getId()
    {
        return id;
    }

    @Override
    public ArtifactId getArtifactId()
    {
        return id;
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
    public String getParent()
    {
        return parent;
    }


    @Override
    public void setParent( final String parent )
    {
        this.parent = parent;
    }


    @Override
    public String getPackage()
    {
        return packageName;
    }


    @Override
    public void setPackageName( final String packageName )
    {
        this.packageName = packageName;
    }


    @Override
    public Architecture getArchitecture()
    {
        return architecture;
    }


    @Override
    public void setArchitecture( final Architecture architecture )
    {
        this.architecture = architecture;
    }


    @Override
    public String getConfigContents()
    {
        return configContents;
    }


    @Override
    public void setConfigContents( final String configContents )
    {
        this.configContents = configContents;
    }


    @Override
    public String getPackagesContents()
    {
        return packagesContents;
    }


    @Override
    public String getOwnerFprint()
    {
        return owner;
    }


    @Override
    public void setPackagesContents( final String packagesContents )
    {
        this.packagesContents = packagesContents;
    }

    @Override
    public long getSize()
    {
        return size;
    }


    @Override
    public void setSize( final long size )
    {
        this.size = size;
    }


    @Override
    public Map<String, String> getExtra()
    {
        return extra;
    }


    @Override
    public void setExtra( final Map<String, String> extra )
    {
        this.extra = extra;
    }


    @Override
    public String serialize()
    {
        return MetadataUtils.JSON.toJson( this );
    }

}
