package ai.subut.kurjun.model.metadata.template;


import java.util.Map;

import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.ArtifactId;


/**
 *
 */
public interface TemplateData extends SerializableMetadata, SubutaiTemplateMetadata
{
    void setOwner( String owner );

    String getUniqId();

    String getContext();

    int getType();

    ArtifactId getArtifactId();

    void setName( String name );

    void setVersion( String version );

    void setParent( String parent );

    void setPackageName( String packageName );

    void setArchitecture( Architecture architecture );

    void setConfigContents( String configContents );

    void setPackagesContents( String packagesContents );

    void setSize( long size );

    void setExtra( Map<String, String> extra );
}
