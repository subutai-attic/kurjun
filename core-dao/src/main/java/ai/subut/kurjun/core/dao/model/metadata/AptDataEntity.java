package ai.subut.kurjun.core.dao.model.metadata;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import ai.subut.kurjun.metadata.common.apt.DefaultDependency;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.apt.AptData;
import ai.subut.kurjun.model.metadata.apt.Dependency;
import ai.subut.kurjun.model.metadata.apt.Priority;
import ai.subut.kurjun.model.repository.ArtifactId;


@Entity
@Table( name = AptDataEntity.TABLE_NAME )
@Access( AccessType.FIELD )
public class AptDataEntity implements AptData
{
    public static final String TABLE_NAME = "debs";

    @EmbeddedId
    RepositoryArtifactId id;

    @Column( name = "owner" )
    private String owner;

    @Column( name = "component" )
    private String component;

    @Column( name = "filename" , nullable = false)
    private String filename;

    @Column( name = "packageName" )
    private String packageName;

    @Column( name = "version" )
    private String version;

    @Column( name = "source" )
    private String source;

    @Column( name = "maintainer" )
    private String maintainer;

    @Enumerated( EnumType.STRING )
    @Column( name = "architecture" )
    private Architecture architecture;

    @Column( name = "installedSize" )
    private int installedSize = 0;


    //@ElementCollection (targetClass = AptDependencyEntity.class)
    @OneToMany(cascade = { CascadeType.ALL}, fetch = FetchType.LAZY, targetEntity = AptDependencyEntity.class)
    @Column( name = "dependencies" )
    private List<Dependency> dependencies = new ArrayList<>();


    //@ElementCollection (targetClass = AptDependencyEntity.class)
    @OneToMany(cascade = { CascadeType.ALL}, fetch = FetchType.LAZY, targetEntity = AptDependencyEntity.class)
    @Column( name = "recommends" )
    private List<Dependency> recommends = new ArrayList<>();


    //@ElementCollection (targetClass = AptDependencyEntity.class)
    @OneToMany(cascade = { CascadeType.ALL}, fetch = FetchType.LAZY, targetEntity = AptDependencyEntity.class)
    @Column( name = "suggests" )
    private List<Dependency> suggests = new ArrayList<>();


    //@ElementCollection (targetClass = AptDependencyEntity.class)
    @OneToMany(cascade = { CascadeType.ALL}, fetch = FetchType.LAZY, targetEntity = AptDependencyEntity.class)
    @Column( name = "enhances" )
    private List<Dependency> enhances = new ArrayList<>();


    //@ElementCollection (targetClass = AptDependencyEntity.class)
    @OneToMany(cascade = { CascadeType.ALL}, fetch = FetchType.LAZY, targetEntity = AptDependencyEntity.class)
    @Column( name = "preDepends" )
    private List<Dependency> preDepends = new ArrayList<>();


    //@ElementCollection (targetClass = AptDependencyEntity.class)
    @OneToMany(cascade = { CascadeType.ALL}, fetch = FetchType.LAZY, targetEntity = AptDependencyEntity.class)
    @Column( name = "conflicts" )
    private List<Dependency> conflicts = new ArrayList<>();


    //@ElementCollection (targetClass = AptDependencyEntity.class)
    @OneToMany(cascade = { CascadeType.ALL}, fetch = FetchType.LAZY, targetEntity = AptDependencyEntity.class)
    @Column( name = "breaks" )
    private List<Dependency> breaks = new ArrayList<>();


    //@ElementCollection (targetClass = AptDependencyEntity.class)
    @OneToMany(cascade = { CascadeType.ALL}, fetch = FetchType.LAZY, targetEntity = AptDependencyEntity.class)
    @Column( name = "replaces" )
    private List<Dependency> replaces = new ArrayList<>();

    @ElementCollection
    @Column( name = "provides" )
    private List<String> provides = new ArrayList<>();

    @Column( name = "section" )
    private String section;

    @Enumerated( EnumType.STRING )
    @Column( name = "priority" )
    private Priority priority;

    @Column( name = "homepage" )
    private String homepage;

    @Lob
    @Column( name = "description" )
    private String description;

    @MapKeyColumn
    @ElementCollection( fetch = FetchType.LAZY )
    @Column( name = "extra" )
    private Map<String, String> extra = new HashMap<>();


    public AptDataEntity()
    {

    }

    public AptDataEntity( String md5Sum, String context , int type)
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
    public ArtifactId getId()
    {
        return id;
    }

    @Override
    public String getUniqId()
    {
        return ( this.id != null ) ? this.id.getContext() + "." + this.id.getMd5Sum() : "";
    }


    @Override
    public String getMd5Sum()
    {
        return ( this.id != null ) ? this.id.getMd5Sum() : "";
    }


    @Override
    public String getComponent()
    {
        return component;
    }


    @Override
    public void setComponent( String component )
    {
        this.component = component;
    }


    @Override
    public String getFilename()
    {
        return filename;
    }


    @Override
    public void setFilename( String filename )
    {
        this.filename = filename;
    }


    @Override
    public String getPackage()
    {
        return packageName;
    }


    @Override
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


    @Override
    public void setVersion( String version )
    {
        this.version = version;
    }


    @Override
    public String getSource()
    {
        return source;
    }


    @Override
    public void setSource( String source )
    {
        this.source = source;
    }


    @Override
    public String getMaintainer()
    {
        return maintainer;
    }


    @Override
    public void setMaintainer( String maintainer )
    {
        this.maintainer = maintainer;
    }


    @Override
    public Architecture getArchitecture()
    {
        return architecture;
    }


    @Override
    public void setArchitecture( Architecture architecture )
    {
        this.architecture = architecture;
    }


    @Override
    public int getInstalledSize()
    {
        return installedSize;
    }


    @Override
    public void setInstalledSize( int installedSize )
    {
        this.installedSize = installedSize;
    }


    @Override
    public List<Dependency> getDependencies()
    {
        return dependencies;
    }


    @Override
    public void setDependencies( List<Dependency> dependencies )
    {
        this.dependencies = dependencies;
    }


    @Override
    public List<Dependency> getRecommends()
    {
        return recommends;
    }


    @Override
    public void setRecommends( List<Dependency> recommends )
    {
        this.recommends = recommends;
    }


    @Override
    public List<Dependency> getSuggests()
    {
        return suggests;
    }


    @Override
    public void setSuggests( List<Dependency> suggests )
    {
        this.suggests = suggests;
    }


    @Override
    public List<Dependency> getEnhances()
    {
        return enhances;
    }


    @Override
    public void setEnhances( List<Dependency> enhances )
    {
        this.enhances = enhances;
    }


    @Override
    public List<Dependency> getPreDepends()
    {
        return preDepends;
    }


    @Override
    public void setPreDepends( List<Dependency> preDepends )
    {
        this.preDepends = preDepends;
    }


    @Override
    public List<Dependency> getConflicts()
    {
        return conflicts;
    }


    @Override
    public void setConflicts( List<Dependency> conflicts )
    {
        this.conflicts = conflicts;
    }


    @Override
    public List<Dependency> getBreaks()
    {
        return breaks;
    }

    @Override
    public void setBreaks( List<Dependency> breaks )
    {
        this.breaks = breaks;
    }


    @Override
    public List<Dependency> getReplaces()
    {
        return replaces;
    }


    @Override
    public void setReplaces( List<Dependency> replaces )
    {
        this.replaces = replaces;
    }


    @Override
    public List<String> getProvides()
    {
        return provides;
    }


    @Override
    public void setProvides( List<String> provides )
    {
        this.provides = provides;
    }


    @Override
    public String getSection()
    {
        return section;
    }


    @Override
    public void setSection( String section )
    {
        this.section = section;
    }


    @Override
    public Priority getPriority()
    {
        return priority;
    }


    @Override
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


    @Override
    public void setHomepage( URL homepage )
    {
        this.homepage = homepage.toString();
    }


    @Override
    public String getDescription()
    {
        return description;
    }


    @Override
    public void setDescription( String description )
    {
        this.description = description;
    }


    @Override
    public Map<String, String> getExtra()
    {
        return extra;
    }


    @Override
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
    public String toString()
    {
        return "DefaultPackageMetadata{" +
                "md5=" + id.getMd5Sum() +
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
