package ai.subut.kurjun.model.metadata.template;


import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.Metadata;


/**
 * Interface for Subutai template meta data.
 *
 */
public interface TemplateMetadata extends Metadata
{

    /**
     * Gets templates architecture.
     *
     * @return template architecture
     */
    Architecture getArchitecture();
}

