package ai.subut.kurjun.model.repository;


import java.util.Set;


/**
 * A Repository that combines two or more Repositories.
 */
public interface UnifiedRepository extends Repository
{
    Set<Repository> getRepositories();
}
