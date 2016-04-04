package ai.subut.kurjun.repo;


import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.core.dao.service.metadata.RepositoryDataService;
import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.repo.service.RepositoryManager;



/**
 *
 */
@Singleton
public class RepositoryManagerImpl implements RepositoryManager
{

    private RepositoryDataService repositoryDataService;


    @Inject
    public RepositoryManagerImpl(RepositoryDataService repositoryDataService)
    {
        this.repositoryDataService = repositoryDataService;
    }

    //*************************************************
    @Override
    public List<RepositoryData>  getRepositoryList()
    {
        return repositoryDataService.getRepositoryList();
    }

}