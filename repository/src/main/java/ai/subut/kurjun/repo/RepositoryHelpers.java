package ai.subut.kurjun.repo;


import ai.subut.kurjun.model.repository.Repository;
import ai.subut.kurjun.model.repository.UnifiedRepository;


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
     * Checks is supplied repository is an apt repository. For unified repositories, child repositories are checked.
     *
     * @param repository repository to check
     * @return {@code true} if supplied repository is an apt repo; {@code false} otherwise
     */
    public static boolean isAptRepository( Repository repository )
    {
        if ( repository instanceof UnifiedRepository )
        {
            UnifiedRepository unified = ( UnifiedRepository ) repository;
            if ( !unified.getRepositories().stream().allMatch( r -> isAptRepository( r ) ) )
            {
                return false;
            }
            if ( !unified.getSecondaryRepositories().stream().allMatch( r -> isAptRepository( r ) ) )
            {
                return false;
            }
            return true;
        }
        return repository instanceof LocalAptRepository
                || repository instanceof LocalAptRepositoryWrapper
                || repository instanceof RemoteAptRepository;
    }

}

