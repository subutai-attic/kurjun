package ai.subut.kurjun.model.metadata.template;


import java.util.Map;

import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.Metadata;


/**
 * Interface for Subutai metadata meta data.
 *
 */
public interface SubutaiTemplateMetadata extends Metadata
{

    /**
     * Gets templates architecture.
     *
     * @return metadata architecture
     */
    Architecture getArchitecture();


    /**
     * Gets parent metadata name.
     *
     * @return parent metadata name
     */
    String getParent();


    /**
     * Gets package with absolute path.
     *
     * @return package with absolute path
     */
    String getPackage();


    /**
     * Gets contents of the "config" file of this metadata.
     *
     * @return contents of the "config" file
     */
    String getConfigContents();


    /**
     * Gets contents of the "packages" file of this metadata.
     *
     * @return contents of the "packages" file
     */
    String getPackagesContents();


    /**
     * Gets metadata's owner fingerprint
     * @return owner
     */
    String getOwnerFprint();


    /**
     * Gets additional properties map of the Subutai metadata.
     *
     * @return map of property values
     */
    Map< String, String> getExtra();

    /**
     * Gets the the size of the package in bytes
     * @return long size
     * */
    long getSize();
}
