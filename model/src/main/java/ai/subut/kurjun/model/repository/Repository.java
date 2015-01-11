package ai.subut.kurjun.model.repository;


import java.net.URL;
import java.util.Set;

import ai.subut.kurjun.model.index.Distribution;


/**
 * Interface to represent a Repository.
 */
public interface Repository {

    /**
     * Gets this Repository's URL which is composed from the protocol, port, server, and path components.
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
     * Gets whether or not the repository is using a secure (confidential) transport
     * protocol.
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
     * Checks to see if this Repository is of the Kurjun type with extra
     * functionality.
     *
     * @return true if of Kurjun type, false otherwise
     */
    boolean isKurjun();


    /**
     * Gets the set of release distributions in this repository.
     *
     * @return a set of release distributions
     */
    Set<Distribution> getDistributions();
}
