package ai.subut.kurjun.core.dao.service.metadata;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.core.dao.api.DAOException;
import ai.subut.kurjun.core.dao.api.metadata.AptDAO;
import ai.subut.kurjun.core.dao.api.metadata.RawDAO;
import ai.subut.kurjun.core.dao.api.metadata.RepositoryDAO;
import ai.subut.kurjun.core.dao.api.metadata.TemplateDAO;
import ai.subut.kurjun.core.dao.model.metadata.AptDataEntity;
import ai.subut.kurjun.core.dao.model.metadata.AptDependencyEntity;
import ai.subut.kurjun.core.dao.model.metadata.RawDataEntity;
import ai.subut.kurjun.core.dao.model.metadata.RepositoryDataId;
import ai.subut.kurjun.core.dao.model.metadata.TemplateDataEntity;
import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.model.identity.ObjectType;
import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.model.metadata.apt.AptData;
import ai.subut.kurjun.model.metadata.apt.Dependency;
import ai.subut.kurjun.model.metadata.apt.PackageMetadata;
import ai.subut.kurjun.model.metadata.raw.RawData;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.model.metadata.template.TemplateData;
import ai.subut.kurjun.model.repository.ArtifactId;


/**
 *
 */
@Singleton
public class RepositoryDataServiceImpl implements RepositoryDataService
{
    private RepositoryDAO repositoryDAO;
    private TemplateDAO templateDAO;
    private RawDAO rawDAO;
    private AptDAO aptDAO;


    @Inject
    public RepositoryDataServiceImpl( RepositoryDAO repositoryDAO, TemplateDAO templateDAO, RawDAO rawDAO,
                                      AptDAO aptDAO )
    {
        this.repositoryDAO = repositoryDAO;
        this.templateDAO = templateDAO;
        this.rawDAO = rawDAO;
        this.aptDAO = aptDAO;
    }

    public RepositoryDataServiceImpl( EntityManagerFactory emf )
    {
        this.repositoryDAO = new RepositoryDAO(emf);
        this.templateDAO = new TemplateDAO(emf);
        this.rawDAO = new RawDAO(emf);
        this.aptDAO = new AptDAO(emf);
    }


    //***************************
    @Override
    public List<RepositoryData> getRepositoryList( int repoType )
    {
        try
        {
            if(repoType == ObjectType.All.getId())
                return repositoryDAO.findAll( "RepositoryDataEntity" );
            else
            {
                return repositoryDAO.findByRepository( repoType );
            }
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
                    templateDAO.merge( (TemplateData)metadata );
                }
                else if ( repoData.getType() == ObjectType.RawRepo.getId() )
                {
                    rawDAO.merge( (RawData)metadata );
                }
                else if ( repoData.getType() == ObjectType.AptRepo.getId() )
                {
                    aptDAO.merge( (AptData )metadata );
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
                templateDAO.merge( (TemplateData)metadata );
            }
            else if ( repoData.getType() == ObjectType.RawRepo.getId() )
            {
                rawDAO.merge( (RawData)metadata );
            }
            else if ( repoData.getType() == ObjectType.AptRepo.getId() )
            {
                aptDAO.merge( (AptData )metadata );
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
                templateDAO.remove( ( TemplateData ) artifact );
            }
            else if ( repoType == ObjectType.RawRepo.getId() )
            {
                rawDAO.remove( ( RawData ) artifact );
            }
            else if ( repoType == ObjectType.AptRepo.getId() )
            {
                aptDAO.remove( ( AptData ) artifact );
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
            else if ( repoType == ObjectType.RawRepo.getId() )
            {
                return rawDAO.find( id );
            }
            else if ( repoType == ObjectType.AptRepo.getId() )
            {
                return aptDAO.find( id );
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
    public List<Object> getAllArtifacts( RepositoryData repoData )
    {
        try
        {
            if ( repoData.getType() == ObjectType.TemplateRepo.getId() )
            {
                List<TemplateData> items = templateDAO.findByRepository( repoData.getContext(), repoData.getType() );

                if ( !items.isEmpty() )
                {
                    return new ArrayList<Object>( items );
                }
            }
            else if ( repoData.getType() == ObjectType.RawRepo.getId() )
            {
                List<RawData> items = rawDAO.findByRepository( repoData.getContext(), repoData.getType() );

                if ( !items.isEmpty() )
                {
                    return new ArrayList<Object>( items );
                }
            }
            else if ( repoData.getType() == ObjectType.AptRepo.getId() )
            {
                List<AptData> items = aptDAO.findByRepository( repoData.getContext(), repoData.getType() );

                if ( !items.isEmpty() )
                {
                    return new ArrayList<Object>( items );
                }
            }
        }
        catch ( Exception ex )
        {
            return Collections.emptyList();
        }

        return Collections.emptyList();
    }


    //***************************
    @Override
    public TemplateData createTemplateDataFromMetaData( RepositoryData repoData, SubutaiTemplateMetadata metadata )
    {
        TemplateData m = new TemplateDataEntity( metadata.getOwner(), repoData.getContext(), repoData.getType() );

        m.setOwner( metadata.getOwner() );
        m.setName( metadata.getName() );
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


    //***************************
    @Override
    public TemplateData createTemplateData( RepositoryData repoData, Object metadata )
    {
        if ( metadata instanceof SubutaiTemplateMetadata )
        {
            return createTemplateDataFromMetaData( repoData, ( SubutaiTemplateMetadata ) metadata );
        }
        else
        {
            return ( TemplateData ) metadata;
        }
    }


    //***************************
    @Override
    public RawData createRawData( RepositoryData repoData, String md5, String name, String owner )
    {
        RawData rawData = new RawDataEntity( md5, repoData.getContext(), repoData.getType() );
        rawData.setOwner( owner );
        rawData.setName( name );

        return rawData;
    }


    //***************************
    @Override
    public AptData createAptData( RepositoryData repoData, String md5, String owner )
    {
        AptData data = new AptDataEntity( md5, repoData.getContext(), repoData.getType() );
        data.setOwner( owner );

        return data;
    }


    //***************************
    @Override
    public AptData copyPackageData( PackageMetadata source, AptData target )
    {
        try
        {
            target.setComponent( source.getComponent() );
            target.setFilename( source.getFilename() );
            target.setPackage( source.getPackage() );
            target.setVersion( source.getVersion() );
            target.setSource( source.getSource() );
            target.setMaintainer( source.getMaintainer() );
            target.setArchitecture( source.getArchitecture() );
            target.setInstalledSize( source.getInstalledSize() );
            target.setDependencies( cloneDependencies( source.getDependencies() ) );
            target.setRecommends( cloneDependencies( source.getRecommends() ) );
            target.setSuggests( cloneDependencies( source.getSuggests() ) );
            target.setEnhances( cloneDependencies( source.getEnhances() ) );
            target.setPreDepends( cloneDependencies( source.getPreDepends() ) );
            target.setConflicts( cloneDependencies( source.getConflicts() ) );
            target.setBreaks( cloneDependencies( source.getBreaks() ) );
            target.setReplaces( cloneDependencies( source.getReplaces() ) );
            target.setProvides( source.getProvides() != null ? new ArrayList<>( source.getProvides() ) : null );
            target.setSection( source.getSection() );
            target.setPriority( source.getPriority() );
            target.setHomepage( source.getHomepage() );
            target.setDescription( source.getDescription() );
            target.getExtra().putAll( ( ( DefaultPackageMetadata ) source ).getExtra() );
        }
        catch(Exception ignore)
        {
            //ignore
        }

        return target;

    }


    private List<Dependency> cloneDependencies( List<Dependency> dependencies )
    {
        if ( dependencies == null )
        {
            return null;
        }

        List<Dependency> result = new ArrayList<>();
        for ( Dependency dependency : dependencies )
        {
            AptDependencyEntity dep = new AptDependencyEntity();
            dep.setPackage( dependency.getPackage() );
            dep.setVersion( dependency.getVersion() );
            dep.setDependencyOperator( dependency.getDependencyOperator() );
            dep.setAlternatives( cloneDependencies( dependency.getAlternatives() ) );
            result.add( dep );
        }
        return result;
    }

}
