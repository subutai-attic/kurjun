package ai.subut.kurjun.model.metadata;


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
     * @param md5 the md5 sum of the package
     * @return the metadata, or null if no such metadata
     * @throws IOException if there are problems accessing the store
     */
    SerializableMetadata get( byte[] md5 ) throws IOException;


    /**
     * Puts the metadata of a Debian package into the store.
     *
     * @param meta the metadata for the package
     * @return true if the metadata was stored, false if it already existed
     * @throws IOException if there are problems accessing the store
     */
    boolean put( SerializableMetadata meta ) throws IOException;


    /**
     * Removes the meta data associated with the Debian package with the md5 sum.
     *
     * @param md5 the md5 sum of the Debian package
     * @return true if the metadata was removed, false if no such metadata existed
     * @throws IOException if there are problems accessing the store
     */
    boolean remove( byte[] md5 ) throws IOException;


    /**
     * Lists metadata stored in the store.
     *
     * @return {@link MetadataListing} instance that contains first batch of a result set, never {@code null}
     * @throws IOException if there are problems accessing the store
     */
    MetadataListing list() throws IOException;


    /**
     * Lists next batch of metadata when previous retrieval returned a truncated result.
     *
     * @param listing listing result from the previous query
     * @return {@link MetadataListing} instance that contains next batch of a result set, never {@code null}
     * @throws IOException if there are problems accessing the store
     */
    MetadataListing listNextBatch( MetadataListing listing ) throws IOException;

}
