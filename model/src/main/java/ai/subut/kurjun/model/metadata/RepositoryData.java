package ai.subut.kurjun.model.metadata;


import java.util.List;

import ai.subut.kurjun.model.repository.RepositoryArtifact;


/**
 *
 */
public interface RepositoryData
{

    String getContext();

    int getType();

    String getOwner();

    void setOwner( String owner );

    List<RepositoryArtifact> getArtifacts();

    void setArtifacts( List<RepositoryArtifact> artifacts );
}
