package ai.subut.kurjun.repo;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.storage.factory.FileStoreFactory;
import ai.subut.kurjun.subutai.service.SubutaiTemplateParser;


/**
 * Local repository for Subutai templates.
 *
 */
public class LocalTemplateRepository extends LocalRepositoryBase
{
    private static final Logger LOGGER = LoggerFactory.getLogger( LocalTemplateRepository.class );

    private PackageMetadataStoreFactory metadataStoreFactory;
    private FileStoreFactory fileStoreFactory;
    private SubutaiTemplateParser templateParser;

    private KurjunContext context;


    @Inject
    public LocalTemplateRepository( PackageMetadataStoreFactory metadataStoreFactory,
                                    FileStoreFactory fileStoreFactory,
                                    SubutaiTemplateParser templateParser,
                                    @Assisted KurjunContext context )
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
        throw new UnsupportedOperationException( "Not supported for template repositories." );
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

            byte[] md5 = fileStore.put( temp );

            if ( Arrays.equals( md5, meta.getMd5Sum() ) )
            {
                metadataStore.put( MetadataUtils.serializableTemplateMetadata( meta ) );
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
    

    public Metadata put( InputStream is, CompressionType compressionType, String owner ) throws IOException
    {
        PackageMetadataStore metadataStore = getMetadataStore();
        FileStore fileStore = getFileStore();

        String ext = CompressionType.makeFileExtenstion( compressionType );
        File temp = Files.createTempFile( "template", ext ).toFile();

        try
        {
            Files.copy( is, temp.toPath(), StandardCopyOption.REPLACE_EXISTING );
            SubutaiTemplateMetadata meta = templateParser.parseTemplate( temp );
            
            byte[] md5 = fileStore.put( temp );
            if ( Arrays.equals( md5, meta.getMd5Sum() ) )
            {
                DefaultTemplate dt = MetadataUtils.serializableTemplateMetadata( meta );
                dt.setOwnerFprint( owner );
                metadataStore.put( dt );
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
}

