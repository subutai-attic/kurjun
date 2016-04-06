package ai.subut.kurjun.model.metadata.template;


import java.util.Map;

import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


/**
 *
 */
public interface TemplateData extends SerializableMetadata, SubutaiTemplateMetadata
{
    String getContext();

    int getType();

    void setVersion( String version );

    void setParent( String parent );

    void setPackageName( String packageName );

    void setArchitecture( Architecture architecture );

    void setConfigContents( String configContents );

    void setPackagesContents( String packagesContents );

    void setSize( long size );

    void setExtra( Map<String, String> extra );
}
