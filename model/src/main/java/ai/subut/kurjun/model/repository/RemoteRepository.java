package ai.subut.kurjun.model.repository;


import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.metadata.MetadataCache;


/**
 * Repository interface for those repositories that are not local and refer to other repositories for their content.
 */
public interface RemoteRepository extends Repository
{

    /**
     * Gets identity to be used for requests to the remote repository. Using returned identity is not mandatory and
     * depends on the remote repository implementation details. Return {@code null} if specifying identity for
     * repository is not applicable.
     *
     * @return identity to be used for requests to the remote repository
     */
    User getIdentity();


    /**
     * Gets metadata cache for the remote repository.
     *
     * @return
     */
    MetadataCache getMetadataCache();

    String getMd5();

}

