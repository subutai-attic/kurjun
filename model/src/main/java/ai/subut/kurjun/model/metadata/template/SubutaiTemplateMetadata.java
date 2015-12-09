package ai.subut.kurjun.model.metadata.template;


import java.util.Map;

import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.Metadata;


/**
 * Interface for Subutai template meta data.
 *
 */
public interface SubutaiTemplateMetadata extends Metadata
{

    /**
     * Gets templates architecture.
     *
     * @return template architecture
     */
    Architecture getArchitecture();


    /**
     * Gets parent template name.
     *
     * @return parent template name
     */
    String getParent();


    /**
     * Gets package with absolute path.
     *
     * @return package with absolute path
     */
    String getPackage();


    /**
     * Gets contents of the "config" file of this template.
     *
     * @return contents of the "config" file
     */
    String getConfigContents();


    /**
     * Gets contents of the "packages" file of this template.
     *
     * @return contents of the "packages" file
     */
    String getPackagesContents();


    /**
     * Gets additional properties map of the Subutai template.
     *
     * @return map of property values
     */
    Map< String, String> getExtra();
}

