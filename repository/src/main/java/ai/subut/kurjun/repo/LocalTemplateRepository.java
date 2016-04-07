package ai.subut.kurjun.repo;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.identity.ObjectType;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.model.metadata.template.TemplateData;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.repo.service.RepositoryManager;
import ai.subut.kurjun.storage.factory.FileStoreFactory;
import ai.subut.kurjun.subutai.service.SubutaiTemplateParser;


/**
 * Local repository for Subutai templates.
 */
public class LocalTemplateRepository extends LocalRepositoryBase
{
    private static final Logger LOGGER = LoggerFactory.getLogger( LocalTemplateRepository.class );

    private FileStoreFactory fileStoreFactory;
    private SubutaiTemplateParser templateParser;
    private KurjunContext context;


    @Inject
    RepositoryManager repositoryManager;


    @Inject
    public LocalTemplateRepository( FileStoreFactory fileStoreFactory,
                                    SubutaiTemplateParser templateParser, @Assisted KurjunContext context )
    {
        this.fileStoreFactory = fileStoreFactory;
        this.templateParser = templateParser;
        this.context = context;
    }


    @Override
    public URL getUrl()
    {
        throw new UnsupportedOperationException( "TODO: Not supported yet." );
    }


    @Override
    public boolean isKurjun()
    {
        return true;
    }


    @Override
    public Set<ReleaseFile> getDistributions()
    {
        throw new UnsupportedOperationException( "Not supported for metadata repositories." );
    }


    @Deprecated
    @Override
    public Metadata put( final File file, final CompressionType compressionType,final String context,  final String owner ) throws IOException
    {
        return null;
    }

    @Override
    @Deprecated
    public Metadata put( InputStream is, CompressionType compressionType ) throws IOException
    {
        //*******************
        RepositoryData repoData = getRepositoryData( "", ObjectType.TemplateRepo.getId(), "" );
        //*******************

        FileStore fileStore = getFileStore();

        String ext = CompressionType.makeFileExtenstion( compressionType );
        File temp = Files.createTempFile( "template", ext ).toFile();
        try
        {
            Files.copy( is, temp.toPath(), StandardCopyOption.REPLACE_EXISTING );
            SubutaiTemplateMetadata meta = templateParser.parseTemplate( temp );

            String md5 = fileStore.put( temp );
            if ( md5.equalsIgnoreCase( meta.getMd5Sum() ) )
            {
                //***********************************
                repositoryManager.addArtifactToRepository( repoData ,meta );
                //***********************************
            }
            else
            {
                fileStore.remove( md5 );
                throw new IOException( "Package integrity failure" );
            }
            return meta;
        }
        finally
        {
            temp.delete();
        }
    }


    //TODO files is copied to temp file and gets copied again in put(File)
    public Metadata put( InputStream is, CompressionType compressionType, String context , String owner ) throws IOException
    {
        //*******************
        RepositoryData repoData = getRepositoryData( context, ObjectType.TemplateRepo.getId(), owner );
        //*******************

        FileStore fileStore = getFileStore();

        String ext = CompressionType.makeFileExtenstion( compressionType );
        File temp = Files.createTempFile( "template", ext ).toFile();

        try
        {
            Files.copy( is, temp.toPath(), StandardCopyOption.REPLACE_EXISTING );
            SubutaiTemplateMetadata meta = templateParser.parseTemplate( temp );

            String md5 = fileStore.put( temp );
            if ( md5.equalsIgnoreCase( meta.getMd5Sum() ) )
            {
                //***********************************
                TemplateData templateData = repositoryManager.constructTemplateData( repoData, meta);
                templateData.setOwner( owner );
                templateData.getArtifactId().setMd5Sum( md5 );
                templateData.setSize( temp.length() );

                repositoryManager.addArtifactToRepository( repoData, templateData );
                //***********************************

                return meta;
            }
            else
            {
                fileStore.remove( md5 );
                throw new IOException( "Package integrity failure" );
            }
        }
        finally
        {
            temp.delete();
        }
    }



    public KurjunContext getContext()
    {
        return context;
    }


    @Override
    protected Logger getLogger()
    {
        return LOGGER;
    }


    @Override
    protected RepositoryData getRepositoryData(String repoContext,int type, String owner )
    {
        if( Strings.isNullOrEmpty(repoContext))
        {
            repoContext = context.getName();
        }

        if( Strings.isNullOrEmpty(owner))
        {
            owner = context.getOwner();
        }

        return repositoryManager.getRepositoryData( repoContext, ObjectType.TemplateRepo.getId(), owner, true );
    }


    @Override
    protected FileStore getFileStore()

    {
        return fileStoreFactory.create( context );
    }

}

