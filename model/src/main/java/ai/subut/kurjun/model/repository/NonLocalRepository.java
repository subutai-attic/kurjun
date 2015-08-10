package ai.subut.kurjun.model.repository;


import java.net.URL;


/**
 * Repository interface for those repositories that are not local and refer to other
 * repositories for their content.
 */
public interface NonLocalRepository extends Repository
{

    /**
     * Initializes this repository to refer to the specified URL.
     *
     * @param url URL of the remote repository to refer to
     */
    void init( URL url );

}
