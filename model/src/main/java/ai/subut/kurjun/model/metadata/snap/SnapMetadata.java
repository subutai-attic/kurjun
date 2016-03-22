package ai.subut.kurjun.model.metadata.snap;


import java.util.List;

import ai.subut.kurjun.model.metadata.Metadata;


/**
 * Interface for Ubuntu Core snap package metadata.
 *
 * Refer to https://developer.ubuntu.com/en/snappy/guides/packaging-format-apps/ for more information about snap package
 * metadata.
 *
 */
public interface SnapMetadata extends Metadata
{

    /**
     * Gets the name and email or URL for the person providing support for this package.
     *
     * @return
     */
    String getVendor();


    /**
     * Gets a URL that points to the best place to collaborate with the developer.
     *
     * @return
     */
    String getSource();


    /**
     * Gets a comma-separated list of framework names that are required to install this snap package.
     *
     * @return
     */
    List<Framework> getFrameworks();
}

