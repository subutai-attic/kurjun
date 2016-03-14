package ai.subut.kurjun.web.context;


import ai.subut.kurjun.common.service.KurjunContext;


public interface ArtifactContext
{
    /**
     * Retrieves User repository based on md5 checksum of the artifact
     *
     * @param md5 checksum
     *
     * @return repository name
     */
    KurjunContext getRepository( String md5 );

    /**
     * Store artifact md5 checksum mapped to User repository
     *
     * @param md5 checksum
     * @param userContext context
     *
     * @return true if success false otherwise
     */
    void store( byte[] md5, KurjunContext userContext );

    /**
     * Remove entry from the Context
     */
    void remove( byte[] md5 );

    /**
     * Get repository md5
     *
     * @param repository identifier
     *
     * @return md5
     */
    String getMd5( String repository );

    /***/
    void store( String repository, byte[] md5 );
}
