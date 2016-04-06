package ai.subut.kurjun.core.dao.service.metadata;


import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.metadata.RepositoryArtifactDAO;
import ai.subut.kurjun.core.dao.api.metadata.RepositoryDAO;
import ai.subut.kurjun.core.dao.api.metadata.TemplateDAO;
import ai.subut.kurjun.core.dao.model.metadata.RepositoryArtifactEntity;
import ai.subut.kurjun.core.dao.model.metadata.RepositoryDataId;
import ai.subut.kurjun.core.dao.model.metadata.TemplateDataEntity;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.model.identity.ObjectType;
import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.model.metadata.template.TemplateData;
import ai.subut.kurjun.model.repository.ArtifactId;
import ai.subut.kurjun.model.repository.RepositoryArtifact;


/**
 *
 */
public class RepositoryDataServiceImpl implements RepositoryDataService
{
    private RepositoryDAO repositoryDAO;
    private TemplateDAO templateDAO;
    //private RepositoryArtifactDAO repositoryArtifactDAO;


    @Inject
    public RepositoryDataServiceImpl( RepositoryDAO repositoryDAO, TemplateDAO templateDAO )
    {
        this.repositoryDAO = repositoryDAO;
        this.templateDAO = templateDAO;
    }


    //***************************
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


    //***************************
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


    //***************************
    @Override
    public RepositoryData mergeRepositoryData( RepositoryData repoData )
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


    //***************************
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
    public Object addArtifactToRepository( RepositoryData repoData, Object metadata )
    {
        try
        {
            if ( repoData != null )
            {
                if ( repoData.getType() == ObjectType.TemplateRepo.getId() )
                {
                    templateDAO.merge( ( TemplateData ) metadata );
                }
            }
        }
        catch ( Exception ex )
        {
            return null;
        }

        return metadata;
    }


    //***************************
    @Override
    public Object addArtifactToRepository( int repoType, RepositoryData repoData, Object metadata )
    {
        try
        {
            if ( repoType == ObjectType.TemplateRepo.getId() )
            {
                TemplateData templateData = createTemplateData( repoData, (SubutaiTemplateMetadata)metadata );
                templateDAO.merge( templateData );
            }
        }
        catch ( Exception ex )
        {
            return null;
        }

        return metadata;
    }


    //***************************
    @Override
    public void removeArtifact( int repoType, Object artifact )
    {
        try
        {
            if ( repoType == ObjectType.TemplateRepo.getId() )
            {
                templateDAO.remove( (TemplateData)artifact );
            }
        }
        catch ( Exception ex )
        {
        }
    }


    //***************************
    @Override
    public Object getArtifact( int repoType, ArtifactId id )
    {
        try
        {
            if ( repoType == ObjectType.TemplateRepo.getId() )
            {
                return templateDAO.find( id );
            }
        }
        catch ( Exception ex )
        {
            return null;
        }

        return null;
    }


    //***************************
    @Override
    public TemplateData createTemplateData( RepositoryData repoData, SubutaiTemplateMetadata metadata )
    {
        TemplateData m = new TemplateDataEntity( metadata.getName(), metadata.getOwner(), metadata.getOwner(),
                repoData.getContext(), repoData.getType() );

        m.setVersion( metadata.getVersion() );
        m.setParent( metadata.getParent() );
        m.setPackageName( metadata.getPackage() );
        m.setArchitecture( metadata.getArchitecture() );
        m.setConfigContents( metadata.getConfigContents() );
        m.setPackagesContents( metadata.getPackagesContents() );
        m.setExtra( metadata.getExtra() );
        m.setSize( metadata.getSize() );

        return m;
    }
}
