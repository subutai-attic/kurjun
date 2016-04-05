package ai.subut.kurjun.repo.service;


import java.util.List;

import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.model.repository.RepositoryArtifact;


/**
 *
 */
public interface RepositoryManager
{
    //*************************************************
    List<RepositoryData> getRepositoryList();


    //*************************************************
    RepositoryData  getRepository( String context, int type );


    //*************************************************
    RepositoryData  persistRepositoryData( String context, int type, String ownerFingerprint );


    //*************************************************
    RepositoryData  getRepositoryData( String context, int type, String ownerFingerprint, boolean create );


    //*************************************************
    RepositoryArtifact addArtifactToRepository( RepositoryData repoData, Object metadata );
}
