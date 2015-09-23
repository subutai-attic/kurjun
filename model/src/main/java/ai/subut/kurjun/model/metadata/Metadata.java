package ai.subut.kurjun.model.metadata;


/**
 * This interface represents basic meta data of packages that can be identified by package's MD5 checksum.
 *
 */
public interface Metadata
{

    /**
     * Gets the md5 checksum of the corresponding package.
     *
     * @return the md5 checksum of the package file
     */
    byte[] getMd5Sum();


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


}

