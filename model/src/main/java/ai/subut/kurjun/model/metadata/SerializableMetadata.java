package ai.subut.kurjun.model.metadata;


import java.io.Serializable;


/**
 * Interface for meta data implementation classes that can be serialized to JSON format.
 *
 */
public interface SerializableMetadata extends Serializable, Metadata
{

    /**
     * Serializes this meta data in JSON format.
     *
     * @return JSON string
     */
    String serialize();

}

