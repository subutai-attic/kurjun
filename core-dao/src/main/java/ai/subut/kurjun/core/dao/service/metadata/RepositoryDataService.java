package ai.subut.kurjun.core.dao.service.metadata;


import java.util.List;

import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.model.metadata.apt.AptData;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;
import ai.subut.kurjun.model.metadata.raw.RawData;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.model.metadata.template.TemplateData;
import ai.subut.kurjun.model.repository.ArtifactId;


/**
 *
 */
public interface RepositoryDataService
{

    //***************************
    List<RepositoryData> getRepositoryList( int repoType );


    //***************************
    RepositoryData getRepositoryData( String context, int type );


    //***************************
    RepositoryData mergeRepositoryData( RepositoryData repoData );


    //***************************
    void persistRepositoryData( RepositoryData repoData );


    //***************************
    Object addArtifactToRepository( RepositoryData repoData, Object metadata );


    //***************************
    Object addArtifactToRepository( int repoType, RepositoryData repoData, Object metadata );


    //***************************
    boolean removeArtifact( int repoType, Object artifact );


    //***************************
    boolean removeArtifact( ArtifactId id );


    //***************************
    Object getArtifact( int repoType, ArtifactId id );


    //***************************
    List<Object> getAllArtifacts( RepositoryData repoData );


    //***************************
    TemplateData createTemplateDataFromMetaData( RepositoryData repoData, SubutaiTemplateMetadata metadata );


    //***************************
    TemplateData createTemplateData( RepositoryData repoData, Object metadata );


    //***************************
    RawData createRawData( RepositoryData repoData, String md5, String name, String owner );


    //***************************
    AptData createAptData( RepositoryData repoData, String md5, String owner );


    //***************************
    AptData copyPackageData( PackageMetadata source, AptData target );
}
