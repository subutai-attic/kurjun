package ai.subut.kurjun.model.metadata.template;


import java.io.Serializable;

import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.Metadata;


/**
 * Interface for Subutai template meta data.
 *
 */
public interface TemplateMetadata extends Metadata, Serializable
{

    /**
     * Gets templates architecture.
     *
     * @return template architecture
     */
    Architecture getArchitecture();
}

