package ai.subut.kurjun.core.dao.service.metadata;


import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.metadata.RepositoryArtifactDAO;
import ai.subut.kurjun.core.dao.api.metadata.RepositoryDAO;
import ai.subut.kurjun.core.dao.model.metadata.RepositoryArtifactEntity;
import ai.subut.kurjun.core.dao.model.metadata.RepositoryDataId;
import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.RepositoryArtifact;


/**
 *
 */
public class RepositoryDataServiceImpl implements RepositoryDataService
{
    private RepositoryDAO repositoryDAO;
    private RepositoryArtifactDAO repositoryArtifactDAO;


    @Inject
    public RepositoryDataServiceImpl(RepositoryDAO repositoryDAO, RepositoryArtifactDAO repositoryArtifactDAO)
    {
        this.repositoryDAO = repositoryDAO;
        this.repositoryArtifactDAO = repositoryArtifactDAO;
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


    //***************************
    @Override
    public RepositoryArtifact addArtifactToRepository( RepositoryData repoData, Object metadata )
    {
        RepositoryArtifact artifact = null;
        try
        {
            if(repoData != null)
            {
                SerializableMetadata sMetadata = (SerializableMetadata)metadata;
                artifact = new RepositoryArtifactEntity(sMetadata.getName(),sMetadata.getOwner(),sMetadata.getMd5Sum());
                artifact.setVersion( sMetadata.getVersion() );
                artifact.setTemplateMetada( metadata  );
                repoData.getArtifacts().add( artifact );

                repositoryDAO.merge( repoData );
            }
        }
        catch ( Exception ex )
        {
            return null;
        }

        return artifact;
    }





}
