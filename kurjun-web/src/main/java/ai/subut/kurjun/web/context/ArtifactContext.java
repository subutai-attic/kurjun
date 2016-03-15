package ai.subut.kurjun.web.context;


import java.util.List;
import java.util.Set;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.RemoteRepository;
import ai.subut.kurjun.web.model.RepositoryCache;


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
    void store( String repository, RepositoryCache repositoryCache );

    List<SerializableMetadata> getList( String repository );

    RepositoryCache getRepositoryCache( String repository );

    Set<RemoteRepository> getRemoteTemplateRepositories();

    void addRemoteTemplateRepository( RemoteRepository remoteRepository );


    Set<RemoteRepository> getRemoteRawRepositories();

    void addRemoteRawRepositories( RemoteRepository remoteRepository );


    Set<RemoteRepository> getRemoteAptRepositories();

    void addRemoteAptRepositories(RemoteRepository remoteRepository);
}
