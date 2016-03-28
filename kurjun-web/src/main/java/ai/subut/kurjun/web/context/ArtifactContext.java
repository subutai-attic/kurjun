package ai.subut.kurjun.web.context;


import java.util.Set;

import ai.subut.kurjun.model.repository.RemoteRepository;


public interface ArtifactContext
{


    Set<RemoteRepository> getRemoteTemplateRepositories();

    void addRemoteTemplateRepository( RemoteRepository remoteRepository );


    Set<RemoteRepository> getRemoteRawRepositories();

    void addRemoteRawRepositories( RemoteRepository remoteRepository );


    Set<RemoteRepository> getRemoteAptRepositories();

    void addRemoteAptRepositories(RemoteRepository remoteRepository);
}
