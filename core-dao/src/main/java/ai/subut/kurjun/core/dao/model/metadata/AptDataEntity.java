package ai.subut.kurjun.core.dao.model.metadata;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.apt.Dependency;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;
import ai.subut.kurjun.model.metadata.apt.Priority;


@Entity
@Table( name = AptDataEntity.TABLE_NAME )
@Access( AccessType.FIELD )
public class AptDataEntity implements PackageMetadata, SerializableMetadata
{
    public static final String TABLE_NAME = "debs";

    @Id
    @Column( name = "md5" )
    private String md5;

    @Column( name = "component" )
    private String component;

    @Column( name = "filename" )
    private String filename;

    @Column( name = "packageName" )
    private String packageName;

    @Column( name = "    private String version;\n" )
    private String version;

    @Column( name = "source" )
    private String source;

    @Column( name = "maintainer" )
    private String maintainer;

    @Enumerated( EnumType.STRING )
    @Column( name = "architecture" )
    private Architecture architecture;

    @Column( name = "installedSize" )
    private int installedSize;


    @ElementCollection
    @Column( name = "dependencies" )
    private List<Dependency> dependencies;

    @ElementCollection
    @Column( name = "recommends" )
    private List<Dependency> recommends;

    @ElementCollection
    @Column( name = "suggests" )
    private List<Dependency> suggests;

    @ElementCollection
    @Column( name = "enhances" )
    private List<Dependency> enhances;

    @ElementCollection
    @Column( name = "preDepends" )
    private List<Dependency> preDepends;

    @ElementCollection
    @Column( name = "conflicts" )
    private List<Dependency> conflicts;

    @ElementCollection
    @Column( name = "breaks" )
    private List<Dependency> breaks;

    @ElementCollection
    @Column( name = "replaces" )
    private List<Dependency> replaces;

    @ElementCollection
    @Column( name = "provides" )
    private List<String> provides;

    @Column( name = "section" )
    private String section;

    @Enumerated( EnumType.STRING )
    @Column( name = "priority" )
    private Priority priority;

    @Column( name = "homepage" )
    private String homepage;

    @Column( name = "description" )
    private String description;

    @MapKeyColumn
    @ElementCollection( fetch = FetchType.LAZY )
    @Column( name = "extra" )
    private Map<String, String> extra = new HashMap<>();


    @Override
    public Object getId()
    {
        return md5;
    }


    @Override
    public String getMd5Sum()
    {
        return md5;
    }


    public void setMd5( String md5 )
    {

        this.md5 = md5;
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
        try
        {
            return new URL( homepage );
        }
        catch ( MalformedURLException e )
        {
            return null;
        }
    }


    public void setHomepage( URL homepage )
    {
        this.homepage = homepage.toString();
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


    /**
     * Gets extra meta data associated with this package.
     *
     * @return
     */
    public Map<String, String> getExtra()
    {
        return extra;
    }


    /**
     * Sets extra meta data for this package.
     *
     * @param extra
     */
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
        hash = 79 * hash + Arrays.hashCode( this.md5.getBytes() );
        return hash;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof PackageMetadata )
        {
            PackageMetadata p = ( PackageMetadata ) obj;
            return Arrays.equals( md5.getBytes(), p.getMd5Sum().getBytes() );
        }
        return true;
    }


    @Override
    public String toString()
    {
        return "DefaultPackageMetadata{" +
                "md5=" + md5 +
                ", component='" + component + '\'' +
                ", filename='" + filename + '\'' +
                ", packageName='" + packageName + '\'' +
                ", version='" + version + '\'' +
                ", source='" + source + '\'' +
                ", maintainer='" + maintainer + '\'' +
                ", architecture=" + architecture +
                ", installedSize=" + installedSize +
                ", dependencies=" + dependencies +
                ", recommends=" + recommends +
                ", suggests=" + suggests +
                ", enhances=" + enhances +
                ", preDepends=" + preDepends +
                ", conflicts=" + conflicts +
                ", breaks=" + breaks +
                ", replaces=" + replaces +
                ", provides=" + provides +
                ", section='" + section + '\'' +
                ", priority=" + priority +
                ", homepage=" + homepage +
                ", description='" + description + '\'' +
                ", extra=" + extra +
                '}';
    }
}
