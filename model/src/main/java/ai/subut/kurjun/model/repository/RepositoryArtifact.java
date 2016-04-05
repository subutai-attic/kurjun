package ai.subut.kurjun.model.repository;


import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.RepositoryData;


/**
 *
 */
public interface RepositoryArtifact extends Metadata
{
    String getName();

    String getOwner();

    String getMd5Sum();

    String getVersion();

    void setVersion( String version );

    RepositoryData getRepositoryData();

    void setRepositoryData( RepositoryData repositoryData );

    void setTemplateMetada( Object templateMetada );
}
