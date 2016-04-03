package ai.subut.kurjun.model.metadata;


import java.util.List;


/**
 * In-memory cache for meta data. Useful for non-local repositories to cache meta data.
 */
public interface MetadataCache
{

    /**
     * Gets a list of cached meta data.
     *
     * @return
     */
    List<SerializableMetadata> getMetadataList();


    /**
     * Gets meta data from cache for supplied md5 checksum.
     *
     * @param md5 MD5 checksum to retrieve metadata for
     * @return metadata if found in the cache; {@code null} otherwise
     */
    SerializableMetadata get( String md5 );


    /**
     * Gets meta data from cache for supplied name and version.
     *
     * @param name name for which to get meta data; must be non-null value
     * @param version version of the meta data; may be {@code null}
     * @return metadata if found in the cache; {@code null} otherwise
     */
    SerializableMetadata get( String name, String version );


    /**
     * Updates cache contents by fetching metadata from the repository.
     */
    void refresh();


}

