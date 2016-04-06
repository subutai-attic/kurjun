package ai.subut.kurjun.repo.service;


import java.util.List;

import ai.subut.kurjun.core.dao.model.metadata.RepositoryArtifactId;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.model.repository.ArtifactId;


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
    Object addArtifactToRepository( RepositoryData repoData, Object metadata );


    //*************************************************
    Object  addArtifactToRepository( int repoType, RepositoryData repoData, Object metadata );


    //*************************************************
    void removeArtifact( int repoType, Object artifact );


    //*************************************************
    Object getArtifact( int repoType, ArtifactId id );

    //*************************************************
    ArtifactId constructArtifactAd( RepositoryData repoData, Metadata metadata );
}
