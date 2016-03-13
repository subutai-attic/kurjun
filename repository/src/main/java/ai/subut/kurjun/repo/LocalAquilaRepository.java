package ai.subut.kurjun.repo;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.storage.factory.FileStoreFactory;


class LocalAquilaRepository extends LocalRepositoryBase
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalAquilaRepository.class);

    private PackageMetadataStoreFactory metadataStoreFactory;
    private FileStoreFactory fileStoreFactory;
    private KurjunContext context;

    @Inject
    public LocalAquilaRepository( PackageMetadataStoreFactory metadataStoreFactory,
                                  FileStoreFactory fileStoreFactory,
                                  @Assisted KurjunContext context )
    {
        this.metadataStoreFactory = metadataStoreFactory;
        this.fileStoreFactory = fileStoreFactory;

        this.context = context;
    }

    @Override
    protected Logger getLogger()
    {
        return LOGGER;
    }


    @Override
    protected PackageMetadataStore getMetadataStore()
    {
        return this.metadataStoreFactory.create( context );
    }


    @Override
    protected FileStore getFileStore()
    {
        return this.fileStoreFactory.create( context );
    }


    @Override
    public Metadata put( final InputStream is, final CompressionType compressionType ) throws IOException
    {
        PackageMetadataStore metadataStore = getMetadataStore();
        FileStore fileStore = getFileStore();


        return null;
    }


    @Override
    public Metadata put( final InputStream is, final CompressionType compressionType, final String owner )
            throws IOException
    {
        return null;
    }


    @Override
    public Metadata put( final File file, final CompressionType compressionType, final String owner ) throws IOException
    {
        return null;
    }


    @Override
    public KurjunContext getContext()
    {
        return null;
    }


    @Override
    public URL getUrl()
    {
        return null;
    }


    @Override
    public boolean isKurjun()
    {
        return false;
    }


    @Override
    public Set<ReleaseFile> getDistributions()
    {
        return null;
    }
}
