package ai.subut.kurjun.model.metadata.snap;


import java.io.IOException;
import java.util.List;


/**
 * A store containing the metadata for snap packages. The md5 checksum of the snap package associated with the stored
 * metadata is used as the primary key to access it.
 *
 */
public interface SnapMetadataStore
{

    /**
     * Checks if snap package exists in the store.
     *
     * @param md5 md5 checksum of the snap package
     * @return {@code true} if package exists in the store; {@code false} otherwise
     * @throws IOException if there are problems accessing the store
     */
    boolean contains( byte[] md5 ) throws IOException;


    /**
     * Gets metadata of the snap package with supplied md5 checksum.
     *
     * @param md5 md5 checksum of the snap package
     * @return metadata of the snap package if found; {@code null} otherwise
     * @throws IOException if there are problems accessing the store
     */
    SnapMetadata get( byte[] md5 ) throws IOException;


    /**
     * Lists snap packages filtered by supplied filter.
     *
     * @param filter filter to filter packages
     * @return list of packages satisfying the filter, never {@code null}
     * @throws IOException if there are problems accessing the store
     */
    List<SnapMetadata> list( SnapMetadataFilter filter ) throws IOException;


    /**
     * Puts metadata of a snap package into the store.
     *
     * @param metadata package metadata to store
     * @return {@code true} if metadata successfully stored; {@code false} if it already existed
     * @throws IOException if there are problems accessing the store
     */
    boolean put( SnapMetadata metadata ) throws IOException;


    /**
     * Removes the metadata of the snap package.
     *
     * @param md5 md5 checksum of the package whose metadata is to be removed
     * @return {@code true} if metadata removed; {@code false} if metadata not found
     * @throws IOException if there are problems accessing the store
     */
    boolean remove( byte[] md5 ) throws IOException;
}

