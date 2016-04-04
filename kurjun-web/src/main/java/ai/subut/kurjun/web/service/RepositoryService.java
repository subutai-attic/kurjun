package ai.subut.kurjun.web.service;


import ai.subut.kurjun.model.metadata.RepositoryData;

import java.util.List;


public interface RepositoryService extends BaseService
{
    List<RepositoryData> getRepositoryList();

    List<String> getRepositoryContextList();
}
