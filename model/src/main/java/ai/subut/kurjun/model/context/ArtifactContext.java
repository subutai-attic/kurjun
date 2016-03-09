package ai.subut.kurjun.model.context;


import ai.subut.kurjun.model.user.UserContext;


public interface ArtifactContext
{
    /*
    * Retrieves User repository based on md5 checksum of the artifact
    * @param md5 checksum
    * @return repository name
    * */
    UserContext getRepository( String md5 );

    /*
    * Store artifact md5 checksum mapped to User repository
    * @param md5 checksum
    * @param user context
    * @return true if success false otherwise
    * */
    void store( byte[] md5, UserContext userContext );

    /*
    * Remove entry from the Context
    * */
    void remove( byte[] md5 );
}
