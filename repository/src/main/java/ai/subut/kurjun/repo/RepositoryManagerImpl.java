package ai.subut.kurjun.repo;


import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.core.dao.model.metadata.RepositoryDataEntity;
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


    //*************************************************
    @Override
    public RepositoryData  getRepository(String context, int type)
    {
        return repositoryDataService.getRepositoryData( context, type );
    }


    //*************************************************
    @Override
    public RepositoryData  persistRepositoryData( String context, int type, String ownerFingerprint )
    {
        RepositoryData repositoryData = new RepositoryDataEntity(context, type);
        repositoryData.setOwner( ownerFingerprint );

        return repositoryDataService.mergeRepositoryData( repositoryData );
    }


    //*************************************************
    @Override
    public RepositoryData  getRepositoryData( String context, int type ,String ownerFingerprint, boolean create )
    {
        RepositoryData  repoData = repositoryDataService.getRepositoryData( context, type );

        if(repoData == null && create)
        {
            return persistRepositoryData( context, type,  ownerFingerprint );
        }
        else
        {
            return repoData;
        }
    }

}