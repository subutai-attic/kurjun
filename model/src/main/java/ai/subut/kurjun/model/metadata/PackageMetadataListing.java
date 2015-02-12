package ai.subut.kurjun.model.metadata;


import java.util.Collection;


/**
 * This interface represents a result set of a {@link PackageMetadataStore#list() } method. This interface exposes
 * methods to get a set of retrieved metadata and to identify if the result is final or not.
 */
public interface PackageMetadataListing
{

    /**
     * Gets metadata retrieved in one batch.
     *
     * @return collection of {@link PackageMetadata} instances
     */
    Collection<PackageMetadata> getPackageMetadata();


    /**
     * Gets a mrker object instance to be used for iterative retrievals.
     *
     * @return the marker object instance
     */
    Object getMarker();


    /**
     * Indicates if this result set is truncated. In case of a truncated result use
     * {@link PackageMetadataStore#listNextBatch(PackageMetadataListing)} to retrieve next batch of result set.
     *
     * @return {@code true} if the result set is truncated; {@code false} otherwise
     */
    boolean isTruncated();

}

