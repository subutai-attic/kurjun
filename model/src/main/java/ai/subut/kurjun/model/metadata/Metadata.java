package ai.subut.kurjun.model.metadata;

// TODO: Refactor the type of id from object to custom class

/**
 * This interface represents basic meta data of packages that can be identified by package's MD5 checksum.
 *
 */
public interface Metadata
{
    
    /**
     * Gets package identifier. Mostly it will be a md5 sum but other may implement
     * differently.
     *
     * @return identifier
     */
    String getOwner();

    /**
     * Gets package identifier. Mostly it will be a md5 sum but other may implement
     * differently.
     *
     * @return identifier
     */

    Object getId();

    /**
     * Gets the md5 checksum of the corresponding package.
     *
     * @return the md5 checksum of the package file
     */
    String getMd5Sum();


    /**
     * Gets package name.
     *
     * @return name of the package
     */
    String getName();


    /**
     * Gets package version.
     *
     * @return version of the package
     */
    String getVersion();


    /**
     * Gets package version.
     *
     * @return filePath of the package
     */
    String getFilePath();


}

