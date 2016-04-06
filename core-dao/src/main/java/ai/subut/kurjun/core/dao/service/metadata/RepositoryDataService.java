package ai.subut.kurjun.core.dao.service.metadata;


import java.util.List;

import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.model.metadata.template.TemplateData;
import ai.subut.kurjun.model.repository.ArtifactId;
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
    Object addArtifactToRepository( RepositoryData repoData, Object metadata );

    //***************************
    Object addArtifactToRepository( int repoType, RepositoryData repoData, Object metadata );

    //***************************
    void removeArtifact( int repoType, Object artifact );

    //***************************
    Object getArtifact( int repoType, ArtifactId id );

    //***************************
    TemplateData createTemplateData( RepositoryData repoData, SubutaiTemplateMetadata metadata );
}
