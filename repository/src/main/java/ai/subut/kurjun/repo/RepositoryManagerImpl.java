package ai.subut.kurjun.repo;


import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.core.dao.model.metadata.RepositoryArtifactId;
import ai.subut.kurjun.core.dao.model.metadata.RepositoryDataEntity;
import ai.subut.kurjun.core.dao.service.metadata.RepositoryDataService;
import ai.subut.kurjun.model.identity.ObjectType;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.model.metadata.apt.AptData;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;
import ai.subut.kurjun.model.metadata.raw.RawData;
import ai.subut.kurjun.model.metadata.template.TemplateData;
import ai.subut.kurjun.model.repository.ArtifactId;
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
        return repositoryDataService.getRepositoryList( ObjectType.All.getId());
    }

    //*************************************************
    @Override
    public List<RepositoryData> getRepositoryList( int repoType )
    {
        return repositoryDataService.getRepositoryList(repoType);
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
    public boolean removeArtifact( int repoType, Object artifact )
    {
        return repositoryDataService.removeArtifact( repoType, artifact );
    }

    //*************************************************
    @Override
    public boolean removeArtifact( ArtifactId id )
    {
        return repositoryDataService.removeArtifact( id);
    }


    //*************************************************
    @Override
    public Object getArtifact( int repoType, ArtifactId id )
    {
        return repositoryDataService.getArtifact( repoType, id );
    }


    //*************************************************
    @Override
    public List<Object> getAllArtifacts( RepositoryData repoData )
    {
        return repositoryDataService.getAllArtifacts( repoData );
    }


    //*************************************************
    @Override
    public ArtifactId constructArtifactId( RepositoryData repoData, Metadata metadata )
    {
        ArtifactId id = new RepositoryArtifactId( metadata.getMd5Sum(), repoData.getContext(), repoData.getType() ) ;
        return id;
    }


    //*************************************************
    @Override
    public ArtifactId constructArtifactId( String context , int repoType , String md5 )
    {
        ArtifactId id = new RepositoryArtifactId( md5, context, repoType ) ;
        return id;
    }


    //*************************************************
    @Override
    public TemplateData constructTemplateData( RepositoryData repoData, Object metadata )
    {
        return repositoryDataService.createTemplateData( repoData, metadata  );
    }


    //*************************************************
    @Override
    public RawData constructRawData( RepositoryData repoData, String md5 , String name , String owner )
    {
        return repositoryDataService.createRawData( repoData, md5 , name ,owner );
    }


    //*************************************************
    @Override
    public AptData constructAptData( RepositoryData repoData, String md5, String owner )
    {
        return repositoryDataService.createAptData( repoData, md5, owner );
    }


    //*************************************************
    @Override
    public AptData copyAptPackage( PackageMetadata source, AptData target)
    {
        return repositoryDataService.copyPackageData(source ,target );
    }

}