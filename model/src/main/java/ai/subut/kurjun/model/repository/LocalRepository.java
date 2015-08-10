package ai.subut.kurjun.model.repository;


/**
 * A non-virtual Repository that is locally hosted on this Kurjun server.
 */
public interface LocalRepository extends Repository
{

    /**
     * Initializes the local repository at the specified base directory.
     *
     * @param baseDirectory base directory of the local apt repo
     */
    void init( String baseDirectory );

}
