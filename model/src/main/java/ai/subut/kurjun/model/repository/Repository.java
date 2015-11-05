package ai.subut.kurjun.model.repository;


import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


/**
 * Interface to represent a Repository.
 */
public interface Repository
{

    /**
     * Gets this Repository's URL which is composed from the protocol, port, server, and path components.
     *
     * @return the URL to this Repository
     */
    URL getUrl();


    /**
     * Gets the Repository's path after the server's hostname and port in the URL.
     *
     * @return the path component of the Repository URL
     */
    String getPath();


    /**
     * Gets the server host serving this Repository.
     *
     * @return the repository server's hostname
     */
    String getHostname();


    /**
     * Gets the server port on which to access this Repository.
     *
     * @return the repository's port
     */
    int getPort();


    /**
     * Gets whether or not the repository is using a secure (confidential) transport protocol.
     *
     * @return true if transport is confidential, false otherwise
     */
    boolean isSecure();


    /**
     * Gets the transport protocol.
     *
     * @return the transport protocol
     */
    Protocol getProtocol();


    /**
     * Checks to see if this Repository is of the Kurjun type with extra functionality.
     *
     * @return true if of Kurjun type, false otherwise
     */
    boolean isKurjun();


    /**
     * Gets the set of release distributions in this repository.
     *
     * @return a set of release distributions
     */
    Set<ReleaseFile> getDistributions();


    /**
     * Gets package info identified by supplied meta data. Refer to
     * {@link Repository#getPackageStream(ai.subut.kurjun.model.metadata.Metadata)} for more info about package
     * identification.
     *
     * @param metadata meta data used to lookup for package
     * @return package meta data if found; {@code null} otherwise
     * @see Repository#getPackageStream(ai.subut.kurjun.model.metadata.Metadata)
     */
    SerializableMetadata getPackageInfo( Metadata metadata );


    /**
     * Gets package data identified by supplied meta data. Unique identifiers like package md5 checksum shall be used
     * for lookup. If unique identifiers are not set, then identifying field collections shall be used like package name
     * and version. When md5 is not set and name has value but without version, then lookup shall be done by name and,
     * if more than one package is found, the one with latest version shall be selected (versions are represented as
     * string, so simple alphabetic comparison is assumed).
     * <p>
     * When supplied meta data does not contain enough identifying information, this method should return {@code null}.
     *
     * @param metadata meta data used to lookup for package
     * @return package stream if found; {@code null} otherwise
     * @see Repository#getPackageInfo(ai.subut.kurjun.model.metadata.Metadata)
     */
    InputStream getPackageStream( Metadata metadata );

}

