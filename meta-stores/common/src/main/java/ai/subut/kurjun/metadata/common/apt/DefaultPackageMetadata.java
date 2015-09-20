package ai.subut.kurjun.metadata.common.apt;


import java.net.URL;
import java.util.Arrays;
import java.util.List;

import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.apt.Dependency;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;
import ai.subut.kurjun.model.metadata.apt.Priority;


/**
 * Simple POJO implementing PackageMetadata.
 *
 */
public class DefaultPackageMetadata implements PackageMetadata, SerializableMetadata
{

    private byte[] md5;
    private String component;
    private String filename;
    private String packageName;
    private String version;
    private String source;
    private String maintainer;
    private Architecture architecture;
    private int installedSize;
    private List<Dependency> dependencies;
    private List<Dependency> recommends;
    private List<Dependency> suggests;
    private List<Dependency> enhances;
    private List<Dependency> preDepends;
    private List<Dependency> conflicts;
    private List<Dependency> breaks;
    private List<Dependency> replaces;
    private List<String> provides;
    private String section;
    private Priority priority;
    private URL homepage;
    private String description;


    @Override
    public byte[] getMd5Sum()
    {
        return Arrays.copyOf( md5, md5.length );
    }


    public void setMd5( byte[] md5 )
    {

        this.md5 = Arrays.copyOf( md5, md5.length );
    }


    @Override
    public String getComponent()
    {
        return component;
    }


    public void setComponent( String component )
    {
        this.component = component;
    }


    @Override
    public String getFilename()
    {
        return filename;
    }


    public void setFilename( String filename )
    {
        this.filename = filename;
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
    public String getName()
    {
        return getPackage();
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
    public String getSource()
    {
        return source;
    }


    public void setSource( String source )
    {
        this.source = source;
    }


    @Override
    public String getMaintainer()
    {
        return maintainer;
    }


    public void setMaintainer( String maintainer )
    {
        this.maintainer = maintainer;
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
    public int getInstalledSize()
    {
        return installedSize;
    }


    public void setInstalledSize( int installedSize )
    {
        this.installedSize = installedSize;
    }


    @Override
    public List<Dependency> getDependencies()
    {
        return dependencies;
    }


    public void setDependencies( List<Dependency> dependencies )
    {
        this.dependencies = dependencies;
    }


    @Override
    public List<Dependency> getRecommends()
    {
        return recommends;
    }


    public void setRecommends( List<Dependency> recommends )
    {
        this.recommends = recommends;
    }


    @Override
    public List<Dependency> getSuggests()
    {
        return suggests;
    }


    public void setSuggests( List<Dependency> suggests )
    {
        this.suggests = suggests;
    }


    @Override
    public List<Dependency> getEnhances()
    {
        return enhances;
    }


    public void setEnhances( List<Dependency> enhances )
    {
        this.enhances = enhances;
    }


    @Override
    public List<Dependency> getPreDepends()
    {
        return preDepends;
    }


    public void setPreDepends( List<Dependency> preDepends )
    {
        this.preDepends = preDepends;
    }


    @Override
    public List<Dependency> getConflicts()
    {
        return conflicts;
    }


    public void setConflicts( List<Dependency> conflicts )
    {
        this.conflicts = conflicts;
    }


    @Override
    public List<Dependency> getBreaks()
    {
        return breaks;
    }


    public void setBreaks( List<Dependency> breaks )
    {
        this.breaks = breaks;
    }


    @Override
    public List<Dependency> getReplaces()
    {
        return replaces;
    }


    public void setReplaces( List<Dependency> replaces )
    {
        this.replaces = replaces;
    }


    @Override
    public List<String> getProvides()
    {
        return provides;
    }


    public void setProvides( List<String> provides )
    {
        this.provides = provides;
    }


    @Override
    public String getSection()
    {
        return section;
    }


    public void setSection( String section )
    {
        this.section = section;
    }


    @Override
    public Priority getPriority()
    {
        return priority;
    }


    public void setPriority( Priority priority )
    {
        this.priority = priority;
    }


    @Override
    public URL getHomepage()
    {
        return homepage;
    }


    public void setHomepage( URL homepage )
    {
        this.homepage = homepage;
    }


    @Override
    public String getDescription()
    {
        return description;
    }


    public void setDescription( String description )
    {
        this.description = description;
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
        hash = 79 * hash + Arrays.hashCode( this.md5 );
        return hash;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof PackageMetadata )
        {
            PackageMetadata p = ( PackageMetadata ) obj;
            return Arrays.equals( md5, p.getMd5Sum() );
        }
        return true;
    }


}

