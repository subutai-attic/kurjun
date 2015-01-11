package ai.subut.kurjun.model.index;


import java.util.List;

import ai.subut.kurjun.model.index.ChecksummedResource;


/**
 * A component of a distribution release: i.e. main, restricted, universe, etc.
 */
public interface Component
{
    /**
     * Gets the name of the component.
     * @return the name
     */
    String getName();


    /**
     * Gets a ordered list of indices as they appear in release files for this component.
     *
     * @return an ordered list of checksummed indices.
     */
    List<ChecksummedResource> getIndicies();


    /**
     * Gets the checksummd index resources by the relative path from the disstribution directory.
     *
     * @param relativePath the relative path from the distribution directory
     * @return the index resource
     */
    ChecksummedResource getIndexResource( String relativePath );
}
