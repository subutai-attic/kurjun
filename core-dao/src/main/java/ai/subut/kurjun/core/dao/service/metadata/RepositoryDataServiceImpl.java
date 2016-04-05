package ai.subut.kurjun.core.dao.service.metadata;


import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.metadata.RepositoryDAO;
import ai.subut.kurjun.core.dao.model.metadata.RepositoryDataId;
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


    @Override
    public RepositoryData getRepositoryData( String context, int type )
    {
        try
        {
            RepositoryDataId id = new RepositoryDataId( context, type );
            return repositoryDAO.find( id );
        }
        catch ( DAOException e )
        {
            return null;
        }
    }


    @Override
    public RepositoryData mergeRepositoryData(RepositoryData repoData)
    {
        try
        {
            return repositoryDAO.merge( repoData );
        }
        catch ( DAOException e )
        {
            return null;
        }
    }


    @Override
    public void persistRepositoryData( RepositoryData repoData )
    {
        try
        {
            repositoryDAO.persist( repoData );
        }
        catch ( DAOException e )
        {
        }
    }




}
