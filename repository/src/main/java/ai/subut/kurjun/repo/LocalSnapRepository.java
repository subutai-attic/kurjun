package ai.subut.kurjun.repo;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.model.metadata.snap.SnapMetadata;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.snap.service.SnapMetadataParser;
import ai.subut.kurjun.storage.factory.FileStoreFactory;


/**
 * Local snap repository implementation.
 */
class LocalSnapRepository extends LocalRepositoryBase
{
    private static final Logger LOGGER = LoggerFactory.getLogger( LocalSnapRepository.class );

    private PackageMetadataStoreFactory metadataStoreFactory;
    private FileStoreFactory fileStoreFactory;
    private SnapMetadataParser metadataParser;

    private KurjunContext context;


    @Inject
    public LocalSnapRepository( PackageMetadataStoreFactory metadataStoreFactory, FileStoreFactory fileStoreFactory,
                                SnapMetadataParser metadataParser, @Assisted KurjunContext context )
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
    public Metadata put( InputStream is, CompressionType compressionType ) throws IOException
    {
        PackageMetadataStore metadataStore = null;//getMetadataStore();
        FileStore fileStore = getFileStore();

        String ext = CompressionType.makeFileExtenstion( compressionType );
        Path temp = Files.createTempFile( "snap-upload", ext );
        try
        {
            Files.copy( is, temp, StandardCopyOption.REPLACE_EXISTING );
            SnapMetadata meta = metadataParser.parse( temp.toFile() );

            String md5 = fileStore.put( temp.toFile() );
            if ( md5.equalsIgnoreCase( meta.getMd5Sum() ) )
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
    public Metadata put( final InputStream is, final CompressionType compressionType,final String context,  final String owner )
            throws IOException
    {
        return null;
    }


    @Override
    public Metadata put( final File file, final CompressionType compressionType, final String context, final String owner ) throws IOException
    {
        return null;
    }


    @Override
    protected Logger getLogger()
    {
        return LOGGER;
    }


    @Override
    protected RepositoryData getRepositoryData( final String repoContext, final int type, String owner )
    {
        return null;
    }



    @Override
    protected FileStore getFileStore()
    {
        return fileStoreFactory.create( context );
    }


    @Override
    public KurjunContext getContext()
    {
        return context;
    }
}

