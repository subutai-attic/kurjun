package ai.subut.kurjun.repo;


import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.core.dao.model.metadata.RepositoryArtifactId;
import ai.subut.kurjun.core.dao.model.metadata.RepositoryDataEntity;
import ai.subut.kurjun.core.dao.service.metadata.RepositoryDataService;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.model.repository.ArtifactId;
import ai.subut.kurjun.model.repository.RepositoryArtifact;
import ai.subut.kurjun.repo.service.RepositoryManager;


/**
 *
 */
@Singleton
public class RepositoryManagerImpl implements RepositoryManager
{

    private RepositoryDataService repositoryDataService;


    @Inject
    public RepositoryManagerImpl( RepositoryDataService repositoryDataService )
    {
        this.repositoryDataService = repositoryDataService;
    }


    //*************************************************
    @Override
    public List<RepositoryData> getRepositoryList()
    {
        return repositoryDataService.getRepositoryList();
    }


    //*************************************************
    @Override
    public RepositoryData getRepository( String context, int type )
    {
        return repositoryDataService.getRepositoryData( context, type );
    }


    //*************************************************
    @Override
    public RepositoryData persistRepositoryData( String context, int type, String ownerFingerprint )
    {
        RepositoryData repositoryData = new RepositoryDataEntity( context, type );
        repositoryData.setOwner( ownerFingerprint );

        return repositoryDataService.mergeRepositoryData( repositoryData );
    }


    //*************************************************
    @Override
    public RepositoryData getRepositoryData( String context, int type, String ownerFingerprint, boolean create )
    {
        RepositoryData repoData = repositoryDataService.getRepositoryData( context, type );

        if ( repoData == null && create )
        {
            return persistRepositoryData( context, type, ownerFingerprint );
        }
        else
        {
            return repoData;
        }
    }


    //*************************************************
    @Override
    public Object addArtifactToRepository( RepositoryData repoData, Object metadata )
    {
        return repositoryDataService.addArtifactToRepository( repoData, metadata );
    }


    //*************************************************
    @Override
    public Object addArtifactToRepository( int repoType, RepositoryData repoData, Object metadata )
    {
        return repositoryDataService.addArtifactToRepository( repoType, repoData, metadata );
    }


    //*************************************************
    @Override
    public void removeArtifact( int repoType, Object artifact )
    {
        repositoryDataService.removeArtifact( repoType, artifact );
    }


    //*************************************************
    @Override
    public Object getArtifact( int repoType, ArtifactId id )
    {
        return repositoryDataService.getArtifact( repoType, id );
    }


    //*************************************************
    @Override
    public ArtifactId constructArtifactAd( RepositoryData repoData, Metadata metadata )
    {
        ArtifactId id = new RepositoryArtifactId( metadata.getName(), metadata.getOwner(), metadata.getMd5Sum(),
                repoData.getContext(), repoData.getType() ) ;
        return id;
    }

}