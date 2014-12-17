package ai.subut.kurjun.model;


import java.io.IOException;


/**
 * A store containing the metadata for Debian packages. The md5 of the Debian
 * package associated with the stored metadata is used as the primary key to
 * access it.
 */
public interface PackageMetadataStore
{
    /**
     * Checks to see if a Debian package's metadata exists within this
     * PkgMetaStore.
     *
     * @param md5 the md5 sum of the Debian package
     * @return true if the metadata of a Debian package having the same md5
     * sum exists
     * @throws IOException if there are problems accessing the store
     */
    boolean contains( byte[] md5 ) throws IOException;


    /**
     * Gets the metadata of a Debian package with the supplied md5 sum.
     *
     * @param md5 the md5 sum of the Debian package
     * @return the metadata, or null of no such metadata
     * @throws IOException if there are problems accessing the store
     */
    PackageMetadata get( byte[] md5 ) throws IOException;


    /**
     * Puts the metadata of a Debian package into the store.
     *
     * @param meta the metadata for the Debian package
     * @return true if the metadata was stored, false if it already existed
     * @throws IOException if there are problems accessing the store
     */
    boolean put( PackageMetadata meta ) throws IOException;


    /**
     * Removes the meta data associated with the Debian package with the md5 sum.
     *
     * @param md5 the md5 sum of the Debian package
     * @return true if the metadata was removed, false if no such metadata existed
     * @throws IOException if there are problems accessing the store
     */
    boolean remove( byte[] md5 ) throws IOException;
}
