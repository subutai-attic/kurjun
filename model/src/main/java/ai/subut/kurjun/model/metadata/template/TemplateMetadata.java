package ai.subut.kurjun.model.metadata.template;


import java.io.Serializable;

import ai.subut.kurjun.model.metadata.Architecture;


/**
 * Interface for Subutai template meta data.
 *
 */
public interface TemplateMetadata extends Serializable
{

    /**
     * Gets template name.
     *
     * @return template name
     */
    String getName();


    /**
     * Gets templates version.
     *
     * @return template version
     */
    String getVersion();


    /**
     * Gets templates architecture.
     *
     * @return template architecture
     */
    Architecture getArchitecture();
}

