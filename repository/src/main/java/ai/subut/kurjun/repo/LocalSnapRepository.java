package ai.subut.kurjun.repo;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.snap.SnapMetadata;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.snap.service.SnapMetadataParser;
import ai.subut.kurjun.storage.factory.FileStoreFactory;


/**
 * Local snap repository implementation.
 *
 */
class LocalSnapRepository extends LocalRepositoryBase
{
    private static final Logger LOGGER = LoggerFactory.getLogger( LocalSnapRepository.class );

    private PackageMetadataStoreFactory metadataStoreFactory;
    private FileStoreFactory fileStoreFactory;
    private SnapMetadataParser metadataParser;

    private KurjunContext context;


    @Inject
    public LocalSnapRepository( PackageMetadataStoreFactory metadataStoreFactory,
                                FileStoreFactory fileStoreFactory,
                                SnapMetadataParser metadataParser,
                                @Assisted KurjunContext context )
    {
        this.metadataStoreFactory = metadataStoreFactory;
        this.fileStoreFactory = fileStoreFactory;
        this.metadataParser = metadataParser;
        this.context = context;
    }


    @Override
    public URL getUrl()
    {
        throw new UnsupportedOperationException( "TODO: set url" );
    }


    @Override
    public boolean isKurjun()
    {
        return true;
    }


    @Override
    public Set<ReleaseFile> getDistributions()
    {
        throw new UnsupportedOperationException( "Not supported for snap repositories." );
    }


    @Override
    public Metadata put( InputStream is ) throws IOException
    {
        return put( is, CompressionType.NONE );
    }


    @Override
    public Metadata put( InputStream is, CompressionType compressionType ) throws IOException
    {
        PackageMetadataStore metadataStore = getMetadataStore();
        FileStore fileStore = getFileStore();

        String ext = null;
        if ( compressionType != null && compressionType != CompressionType.NONE )
        {
            ext = "." + compressionType.getExtension();
        }
        Path temp = Files.createTempFile( "snap-upload", ext );
        try
        {
            Files.copy( is, temp, StandardCopyOption.REPLACE_EXISTING );
            SnapMetadata meta = metadataParser.parse( temp.toFile() );

            byte[] md5 = fileStore.put( temp.toFile() );
            if ( Arrays.equals( md5, meta.getMd5Sum() ) )
            {
                metadataStore.put( MetadataUtils.serializableSnapMetadata( meta ) );
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
            Files.delete( temp );
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

