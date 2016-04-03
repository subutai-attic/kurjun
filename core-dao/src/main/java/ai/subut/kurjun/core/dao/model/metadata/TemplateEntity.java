package ai.subut.kurjun.core.dao.model.metadata;


import java.util.HashMap;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import ai.subut.kurjun.metadata.common.subutai.TemplateId;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;


@Entity
@Table( name = TemplateEntity.TABLE_NAME )
@Access( AccessType.FIELD )
//@IdClass(TemplatePk.class)
public class TemplateEntity implements SerializableMetadata, SubutaiTemplateMetadata
{
    public static final String TABLE_NAME = "templates";


    @Id
    @Column( name = "ID" )
    private String id;

    @Column( name = "md5Sum" )
    private String md5Sum;

    @Column( name = "name" )
    private String name;

    @Column( name = "version" )
    private String version;

    @Column( name = "parent" )
    private String parent;

    @Column( name = "packageName" )
    private String packageName;

    @Column( name = "architecture" )
    private Architecture architecture;

    @Column( name = "configContents" )
    private String configContents;

    @Column( name = "packagesContents" )
    private String packagesContents;

    @Column( name = "ownerFprint" )
    private String ownerFprint;

    @Column( name = "size" )
    private long size;

    @ElementCollection( fetch = FetchType.LAZY )
    @MapKeyColumn
    @Column( name = "extra" )
    private Map<String, String> extra = new HashMap<>();


    @Override
    public String getId()
    {
        if ( ownerFprint != null && md5Sum != null )
        {
            return new TemplateId( ownerFprint, md5Sum ).get();
        }
        else
        {
            return null;
        }
    }


    public void setId( String ownerFprint, String md5Sum )
    {
        this.ownerFprint = ownerFprint;
        this.md5Sum = md5Sum;
    }


    @Override
    public String getMd5Sum()
    {
        return md5Sum;
    }


    public void setMd5Sum( final String md5Sum )
    {
        this.md5Sum = md5Sum;
    }


    @Override
    public String getName()
    {
        return name;
    }


    public void setName( final String name )
    {
        this.name = name;
    }


    @Override
    public String getVersion()
    {
        return version;
    }


    public void setVersion( final String version )
    {
        this.version = version;
    }


    public String getParent()
    {
        return parent;
    }


    public void setParent( final String parent )
    {
        this.parent = parent;
    }


    @Override
    public String getPackage()
    {
        return packageName;
    }


    public String getPackageName()
    {
        return packageName;
    }


    public void setPackageName( final String packageName )
    {
        this.packageName = packageName;
    }


    public Architecture getArchitecture()
    {
        return architecture;
    }


    public void setArchitecture( final Architecture architecture )
    {
        this.architecture = architecture;
    }


    @Override
    public String getConfigContents()
    {
        return configContents;
    }


    public void setConfigContents( final String configContents )
    {
        this.configContents = configContents;
    }


    @Override
    public String getPackagesContents()
    {
        return packagesContents;
    }


    public void setPackagesContents( final String packagesContents )
    {
        this.packagesContents = packagesContents;
    }


    @Override
    public String getOwnerFprint()
    {
        return ownerFprint;
    }


    public void setOwnerFprint( final String ownerFprint )
    {
        this.ownerFprint = ownerFprint;
    }


    @Override
    public long getSize()
    {
        return size;
    }


    public void setSize( final long size )
    {
        this.size = size;
    }


    public Map<String, String> getExtra()
    {
        return extra;
    }


    public void setExtra( final Map<String, String> extra )
    {
        this.extra = extra;
    }


    @Override
    public String serialize()
    {
        return MetadataUtils.JSON.toJson( this );
    }


    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 17 * hash + this.md5Sum.hashCode();
        return hash;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof TemplateEntity )
        {
            TemplateEntity other = ( TemplateEntity ) obj;
            return this.md5Sum.equalsIgnoreCase( other.md5Sum );
        }
        return false;
    }
}
