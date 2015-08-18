package ai.subut.kurjun.model.metadata.snap;


import java.util.List;


/**
 * Interface for Ubuntu Core snap package metadata.
 *
 * Refer to https://developer.ubuntu.com/en/snappy/guides/packaging-format-apps/ for more information about snap package
 * metadata.
 *
 */
public interface SnapMetadata
{

    /**
     * Gets the md5 checksum of the corresponding package.
     *
     * @return
     */
    byte[] getMd5();


    /**
     * Gets the name of the snap package.
     *
     * @return snap package name
     */
    String getName();


    /**
     * Gets the version of the snap package.
     *
     * @return
     */
    String getVersion();


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

