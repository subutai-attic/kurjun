package ai.subut.kurjun.repo.service;


import java.util.List;

import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.model.metadata.apt.AptData;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;
import ai.subut.kurjun.model.metadata.raw.RawData;
import ai.subut.kurjun.model.metadata.template.TemplateData;
import ai.subut.kurjun.model.repository.ArtifactId;


/**
 *
 */
public interface RepositoryManager
{
    //*************************************************
    List<RepositoryData> getRepositoryList();


    //*************************************************
    List<RepositoryData> getRepositoryList( int repoType );

    //*************************************************
    RepositoryData getRepository( String context, int type );


    //*************************************************
    RepositoryData persistRepositoryData( String context, int type, String ownerFingerprint );


    //*************************************************
    RepositoryData getRepositoryData( String context, int type, String ownerFingerprint, boolean create );


    //*************************************************
    Object addArtifactToRepository( RepositoryData repoData, Object metadata );


    //*************************************************
    Object addArtifactToRepository( int repoType, RepositoryData repoData, Object metadata );


    //*************************************************
    boolean removeArtifact( int repoType, Object artifact );


    //*************************************************
    boolean removeArtifact( ArtifactId id );


    //*************************************************
    Object getArtifact( int repoType, ArtifactId id );


    //*************************************************
    List<Object> getAllArtifacts( RepositoryData repoData );


    //*************************************************
    ArtifactId constructArtifactId( RepositoryData repoData, Metadata metadata );


    //*************************************************
    ArtifactId constructArtifactId( String context, int repoType, String md5 );


    //*************************************************
    TemplateData constructTemplateData( RepositoryData repoData, Object metadata );


    //*************************************************
    RawData constructRawData( RepositoryData repoData, String md5, String name, String owner );


    //*************************************************
    AptData constructAptData( RepositoryData repoData, String md5, String owner );


    //*************************************************
    AptData copyAptPackage( PackageMetadata source, AptData target );
}
