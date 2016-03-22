package ai.subut.kurjun.repo.service;


import ai.subut.kurjun.model.metadata.Architecture;


/**
 * Parser for filename field of a Debian package.
 *
 */
public interface PackageFilenameParser
{

    /**
     * Gets component from supplied filename field value.
     *
     * @param filename filename field value
     * @return component name if supplied filename is valid; {@code null} otherwise
     */
    String getComponent( String filename );


    /**
     * Gets package name from supplied filename field value.
     *
     * @param filename filename field value
     * @return package name if supplied filename is valid; {@code null} otherwise
     */
    String getPackageFromFilename( String filename );


    /**
     * Gets version from supplied filename field value.
     *
     * @param filename filename field value
     * @return version string if supplied filename is valid; {@code null} otherwise
     */
    String getVersionFromFilename( String filename );


    /**
     * Gets architecture from supplied filename field value.
     *
     * @param filename filename field value
     * @return architecture if supplied filename is valid; {@code null} otherwise
     */
    Architecture getArchFromFilename( String filename );

}

