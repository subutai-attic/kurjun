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

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.core.dao.service.metadata.TemplateDataService;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.model.repository.RepositoryType;
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

    private PackageMetadataStoreFactory metadataStoreFactory;
    private FileStoreFactory fileStoreFactory;
    private SubutaiTemplateParser templateParser;
    private KurjunContext context;

    @Inject
    TemplateDataService templateDataService;

    @Inject
    RepositoryManager repositoryManager;


    @Inject
    public LocalTemplateRepository( PackageMetadataStoreFactory metadataStoreFactory, FileStoreFactory fileStoreFactory,
                                    SubutaiTemplateParser templateParser, @Assisted KurjunContext context )
    {
        this.metadataStoreFactory = metadataStoreFactory;
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


    @Override
    public Metadata put( InputStream is, CompressionType compressionType ) throws IOException
    {
        PackageMetadataStore metadataStore = getMetadataStore();
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
                metadataStore.put( MetadataUtils.serializableTemplateMetadata( meta ) );
                //templateDataService.merge( MetadataUtils.serializableTemplateMetadata( meta ) );
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
        PackageMetadataStore metadataStore = getMetadataStore();
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
                //*******************
                RepositoryData repoData = getRepositoryData(context, RepositoryType.TemplateRepo.getId(), owner);
                //*******************


                DefaultTemplate dt = MetadataUtils.serializableTemplateMetadata( meta );
                dt.setSize( temp.length() );
                dt.setOwnerFprint( owner );

                //***********************************
                metadataStore.put( dt );
                //templateDataService.merge( dt );
                //***********************************

                return dt;
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


    @Override
    public Metadata put( final File file, final CompressionType compressionType,final String context,  final String owner ) throws IOException
    {
        PackageMetadataStore metadataStore = getMetadataStore();
        FileStore fileStore = getFileStore();
        SubutaiTemplateMetadata meta = templateParser.parseTemplate( file );

        try
        {
            String md5 = fileStore.put( file );

            if ( md5.equalsIgnoreCase( meta.getMd5Sum() ) )
            {

                //*******************
                RepositoryData repoData = getRepositoryData(context, RepositoryType.TemplateRepo.getId(), owner);
                //*******************

                DefaultTemplate dt = MetadataUtils.serializableTemplateMetadata( meta );
                dt.setSize( file.length() );
                dt.setOwnerFprint( owner );
                metadataStore.put( dt );

                //***********************************

                //templateDataService.merge( dt );
                //***********************************

                return dt;
            }
            else
            {
                fileStore.remove( md5 );
                throw new IOException( "Package integrity failure" );
            }
        }
        finally
        {
            file.delete();
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
    protected PackageMetadataStore getMetadataStore()
    {
        return metadataStoreFactory.create( context );
    }


    @Override
    protected FileStore getFileStore()

    {
        return fileStoreFactory.create( context );
    }



    //******************************************
    public RepositoryData getRepositoryData(String context, int type, String ownerFingerprint)
    {
        return repositoryManager.getRepositoryData( context, type, ownerFingerprint, true );
    }


}

