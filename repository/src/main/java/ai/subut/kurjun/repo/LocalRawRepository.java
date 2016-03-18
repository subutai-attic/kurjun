package ai.subut.kurjun.repo;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.raw.RawMetadata;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.storage.factory.FileStoreFactory;


public class LocalRawRepository extends LocalRepositoryBase
{

    private static final Logger LOGGER = LoggerFactory.getLogger( LocalRawRepository.class );

    private final PackageMetadataStoreFactory metadataStoreFactory;

    private final FileStoreFactory fileStoreFactory;

    private final KurjunContext context;


    @Inject
    public LocalRawRepository( PackageMetadataStoreFactory metadataStoreFactory, FileStoreFactory fileStoreFactory,
                               @Assisted KurjunContext context )
    {
        this.metadataStoreFactory = metadataStoreFactory;
        this.fileStoreFactory = fileStoreFactory;
        this.context = context;
    }


    @Override
    public Metadata put( InputStream is, CompressionType compressionType ) throws IOException
    {
        throw new UnsupportedOperationException( "Use other put method" );
    }


    @Override
    public Metadata put( final InputStream is, final CompressionType compressionType, final String owner )
            throws IOException
    {
        Objects.requireNonNull( is, "InputStream cannot be null" );

        return null;
    }


    @Override
    public Metadata put( final File file, final CompressionType compressionType, final String owner ) throws IOException
    {
        DefaultMetadata metadata = new DefaultMetadata();
        metadata.setName( file.getName() );

        SerializableMetadata oldmeta = getPackageInfo( metadata );

        if ( oldmeta != null )
        {
            // delete old record having the same file name
            delete( oldmeta.getMd5Sum() );
        }

        byte[] md5 = getFileStore().put( file );

        RawMetadata meta = new RawMetadata( md5, file.getName(), file.length(), owner );
        getMetadataStore().put( meta );

        return meta;
    }

    public Metadata put( final File file, String name, final String owner )
            throws IOException
    {
        DefaultMetadata metadata = new DefaultMetadata();

        metadata.setName( name );

        SerializableMetadata oldmeta = getPackageInfo( metadata );

        if ( oldmeta != null )
        {
            // delete old record having the same file name
            delete( oldmeta.getMd5Sum() );
        }

        byte[] md5 = getFileStore().put( file );

        RawMetadata meta = new RawMetadata( md5, name, file.length(), owner );

        getMetadataStore().put( meta );

        return meta;
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
        throw new UnsupportedOperationException( "Not supported for raw repositories" );
    }


    @Override
    public KurjunContext getContext()
    {
        return null;
    }
}
