package ai.subut.kurjun.core.dao.model.metadata;


import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.codec.binary.Hex;

import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;


@Entity
@Table( name = TemplateEntity.TABLE_NAME )
public class TemplateEntity implements SerializableMetadata, SubutaiTemplateMetadata
{
    public static final String TABLE_NAME = "";


    @Column( name = "md5Sum" )
    private byte[] md5Sum;

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

    @Column( name = "extra" )
    private Map<String, String> extra = new HashMap<>();


    @Override
    public Object getId()
    {
        if ( ownerFprint != null && md5Sum != null )
        {
            return new TemplateId( ownerFprint, Hex.encodeHexString( md5Sum ) ).get();
        }
        else
        {
            return null;
        }
    }

    public void setId( String ownerFprint, byte[] md5Sum )
    {
        this.ownerFprint = ownerFprint;
        this.md5Sum = md5Sum;
    }


    @Override
    public byte[] getMd5Sum()
    {
        return md5Sum;
    }


    public void setMd5Sum( final byte[] md5Sum )
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


    @Override
    public String getPackage()
    {
        return null;
    }


    public void setParent( final String parent )
    {
        this.parent = parent;
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


    public String getConfigContents()
    {
        return configContents;
    }


    public void setConfigContents( final String configContents )
    {
        this.configContents = configContents;
    }


    public String getPackagesContents()
    {
        return packagesContents;
    }


    public void setPackagesContents( final String packagesContents )
    {
        this.packagesContents = packagesContents;
    }


    public String getOwnerFprint()
    {
        return ownerFprint;
    }


    public void setOwnerFprint( final String ownerFprint )
    {
        this.ownerFprint = ownerFprint;
    }


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
        return null;
    }
}
