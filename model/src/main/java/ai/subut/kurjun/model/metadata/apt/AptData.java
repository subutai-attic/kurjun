package ai.subut.kurjun.model.metadata.apt;


import java.net.URL;
import java.util.List;
import java.util.Map;

import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


/**
 *
 */
public interface AptData extends PackageMetadata, SerializableMetadata
{
    void setOwner( String owner );

    String getUniqId();

    void setComponent( String component );

    void setFilename( String filename );

    void setPackage( String packageName );

    void setVersion( String version );

    void setSource( String source );

    void setMaintainer( String maintainer );

    void setArchitecture( Architecture architecture );

    void setInstalledSize( int installedSize );

    void setDependencies( List<Dependency> dependencies );

    void setRecommends( List<Dependency> recommends );

    void setSuggests( List<Dependency> suggests );

    void setEnhances( List<Dependency> enhances );

    void setPreDepends( List<Dependency> preDepends );

    void setConflicts( List<Dependency> conflicts );

    void setBreaks( List<Dependency> breaks );

    void setReplaces( List<Dependency> replaces );

    void setProvides( List<String> provides );

    void setSection( String section );

    void setPriority( Priority priority );

    void setHomepage( URL homepage );

    void setDescription( String description );

    Map<String, String> getExtra();

    void setExtra( Map<String, String> extra );
}
