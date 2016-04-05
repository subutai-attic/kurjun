package ai.subut.kurjun.core.dao.service.metadata;


import java.util.List;

import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.model.repository.RepositoryArtifact;


/**
 *
 */
public interface RepositoryDataService
{

    List<RepositoryData> getRepositoryList();


    RepositoryData getRepositoryData( String context, int type );


    RepositoryData mergeRepositoryData( RepositoryData repoData );


    void persistRepositoryData( RepositoryData repoData );


    //***************************
    RepositoryArtifact addArtifactToRepository( RepositoryData repoData, Object metadata );
}
