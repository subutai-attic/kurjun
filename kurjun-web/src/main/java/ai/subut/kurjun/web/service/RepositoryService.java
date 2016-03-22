package ai.subut.kurjun.web.service;


import ai.subut.kurjun.model.repository.Repository;

import java.util.List;


public interface RepositoryService extends BaseService
{
    List<String> getRepositories();

    List<String> getRepositoryList();
}
