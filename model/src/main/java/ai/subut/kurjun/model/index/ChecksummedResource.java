package ai.subut.kurjun.model.index;


/**
 * A resource, mainly a file, that has one or more checksums associated with it in the release
 * distribution archive.
 */
public interface ChecksummedResource
{
    /**
     * Gets the relative path of the resource relative to the release distribution archive.
     * @return relative path of resource
     */
    String getRelativePath();


    /**
     * Gets the size in bytes of the resource.
     * @return size in bytes
     */
    long getSize();


    /**
     * Gets the checksum value, based on the checksum algorithm.
     *
     * @param type the type of checksum algorithm
     * @return the checksum value based on the type
     */
    byte[] getChecksum( Checksum type );
}
