package ai.subut.kurjun.metadata.common.subutai;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;


/**
 * Default serializable POJO implementation class of {@link SubutaiTemplateMetadata}.
 *
 */
public class DefaultTemplate implements SubutaiTemplateMetadata, SerializableMetadata
{

    private byte[] md5Sum;
    private String name;
    private String version;
    private String parent;
    private String packageName;
    private Architecture architecture;
    private String configContents;
    private String packagesContents;
    private Map< String, String> extra = new HashMap<>();


    @Override
    public byte[] getMd5Sum()
    {
        return md5Sum != null ? Arrays.copyOf( md5Sum, md5Sum.length ) : null;
    }


    public void setMd5Sum( byte[] md5Sum )
    {
        this.md5Sum = md5Sum != null ? Arrays.copyOf( md5Sum, md5Sum.length ) : null;
    }


    @Override
    public String getName()
    {
        return name;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    @Override
    public String getVersion()
    {
        return version;
    }


    public void setVersion( String version )
    {
        this.version = version;
    }


    @Override
    public Architecture getArchitecture()
    {
        return architecture;
    }


    public void setArchitecture( Architecture architecture )
    {
        this.architecture = architecture;
    }


    @Override
    public String getParent()
    {
        return parent;
    }


    public void setParent( String parent )
    {
        this.parent = parent;
    }


    @Override
    public String getPackage()
    {
        return packageName;
    }


    public void setPackage( String packageName )
    {
        this.packageName = packageName;
    }


    @Override
    public String getConfigContents()
    {
        return configContents;
    }


    public void setConfigContents( String configContents )
    {
        this.configContents = configContents;
    }


    @Override
    public String getPackagesContents()
    {
        return packagesContents;
    }


    public void setPackagesContents( String packagesContents )
    {
        this.packagesContents = packagesContents;
    }


    @Override
    public Map<String, String> getExtra()
    {
        return extra;
    }


    public void setExtra( Map<String, String> extra )
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
        hash = 17 * hash + Arrays.hashCode( this.md5Sum );
        return hash;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof DefaultTemplate )
        {
            DefaultTemplate other = ( DefaultTemplate ) obj;
            return Arrays.equals( this.md5Sum, other.md5Sum );
        }
        return false;
    }


}

