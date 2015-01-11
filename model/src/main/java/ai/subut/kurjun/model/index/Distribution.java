package ai.subut.kurjun.model.index;


import java.net.URL;
import java.util.List;

import ai.subut.kurjun.model.metadata.Architecture;


/**
 * One of potentially many release distributions contained in a Repository.
 */
public interface Distribution
{
    String[] PROPERTY_ORDER = {
        "Origin", "Label", "Suite", "Version", "Codename", "Date", "Architectures",
        "Components", "Description", "MD5Sum", "SHA1", "SHA256"
    };


    /**
     * Gets the origin (a.k.a the organization) of the distribution.
     */
    String getOrigin();


    /**
     * Gets the label. I.e. usually the same as the origin: Debian, Ubuntu.
     * @todo explain more what this really is used for.
     */
    String getLabel();


    /**
     * Gets the suite. I.e. Debian uses stable, Ubuntu uses trusty
     * @todo explain more what this really is used for.
     */
    String getSuite();


    /**
     * Gets the release version.
     */
    String getVersion();


    /**
     * Gets the release code name.
     */
    String getCodename();


    /**
     * Gets the date of the release. i.e. 'Thu, 08 May 2014 14:19:09 UTC'
     */
    String getDate();


    /**
     * Gets the architectures this release distribution supports.
     */
    List<Architecture> getArchitectures();


    /**
     * Gets the components in the release distribution: i.e. main, contrib, non-free.
     */
    List<String> getComponents();


    /**
     * Gets the description for the release distribution.
     */
    String getDescription();


    /**
     * Gets a list of all the indices in the order they appear: breath first alphabetical. From the resource one
     * can acquire the desired checksum.
     *
     * @return the index resources
     */
    List<ChecksummedResource> getIndices();


    /**
     * Gets the checksummed index resource by it's path relative to the distribution directory.
     *
     * @param relativePath the path relative to the distribution directory
     * @return the checksummed index resource
     */
    ChecksummedResource getIndexResource( String relativePath );


    /**
     * Gets a component by name.
     * @param compName the name of the component to get
     * @return the component with the specified name or null if no such component exists
     */
    Component getComponent( String compName );


    /**
     * Gets the URL to the release file.
     * @return the URL to the release file
     */
    URL getReleaseFile();
}
