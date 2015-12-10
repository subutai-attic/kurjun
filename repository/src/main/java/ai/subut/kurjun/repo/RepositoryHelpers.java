package ai.subut.kurjun.repo;


import ai.subut.kurjun.model.repository.Repository;


/**
 * Utility class for repository related helper methods.
 *
 */
public class RepositoryHelpers
{


    RepositoryHelpers()
    {
        // not to be initialized
    }


    /**
     * Checks is supplied repository is an apt repository.
     *
     * @param repository repository to check
     * @return {@code true} if supplied repository is an apt repo; {@code false} otherwise
     */
    public static boolean isAptRepository( Repository repository )
    {
        return repository instanceof LocalAptRepository
                || repository instanceof LocalAptRepositoryWrapper
                || repository instanceof NonLocalAptRepository;
    }

}

