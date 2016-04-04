package ai.subut.kurjun.core.dao.service.metadata;


import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.metadata.RepositoryDAO;
import ai.subut.kurjun.model.metadata.RepositoryData;


/**
 *
 */
public class RepositoryDataServiceImpl implements RepositoryDataService
{
    private RepositoryDAO repositoryDAO;


    @Inject
    public RepositoryDataServiceImpl(RepositoryDAO repositoryDAO)
    {
        this.repositoryDAO = repositoryDAO;
    }


    @Override
    public List<RepositoryData> getRepositoryList()
    {
        try
        {
            return repositoryDAO.findAll( "RepositoryDataEntity" );
        }
        catch ( DAOException e )
        {
            return Collections.emptyList();
        }
    }
}
