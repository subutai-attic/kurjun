package ai.subut.kurjun.repo;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.model.identity.ObjectType;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.RepositoryData;
import ai.subut.kurjun.model.metadata.raw.RawData;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.repo.service.RepositoryManager;
import ai.subut.kurjun.storage.factory.FileStoreFactory;


public class LocalRawRepository extends LocalRepositoryBase
{

    private static final Logger LOGGER = LoggerFactory.getLogger( LocalRawRepository.class );

    private final PackageMetadataStoreFactory metadataStoreFactory;

    private final FileStoreFactory fileStoreFactory;

    private final KurjunContext context;

    @Inject
    RepositoryManager repositoryManager;


    @Inject
    public LocalRawRepository( PackageMetadataStoreFactory metadataStoreFactory, FileStoreFactory fileStoreFactory,
                               @Assisted KurjunContext context )
    {
        this.metadataStoreFactory = metadataStoreFactory;
        this.fileStoreFactory = fileStoreFactory;
        this.context = context;
    }


    @Deprecated
    @Override
    public Metadata put( InputStream is, CompressionType compressionType ) throws IOException
    {
        throw new UnsupportedOperationException( "Use other put method" );
    }


    @Deprecated
    @Override
    public Metadata put( final InputStream is, final CompressionType compressionType, final String context,
                         final String owner ) throws IOException
    {
        Objects.requireNonNull( is, "InputStream cannot be null" );

        return null;
    }


    @Deprecated
    @Override
    public Metadata put( final File file, final CompressionType compressionType, final String context,
                         final String owner ) throws IOException
    {
        return null;
    }


    public Metadata put( final File file, String name, final String context, String owner ) throws IOException
    {

        //*******************
        RepositoryData repoData = getRepositoryData( context, ObjectType.RawRepo.getId(), owner );
        //*******************

        String md5 = getFileStore().put( file );
        RawData rawData = repositoryManager.constructRawData ( repoData, md5 , name , owner );
        rawData.setSize( file.length() );

        repositoryManager.addArtifactToRepository( repoData, rawData );

        return rawData;
    }


    @Override
    protected Logger getLogger()
    {
        return LOGGER;
    }


    @Override
    protected RepositoryData getRepositoryData( String repoContext, int type, String owner )
    {

        if ( Strings.isNullOrEmpty( repoContext ) )
        {
            repoContext = context.getName();
        }

        if ( Strings.isNullOrEmpty( owner ) )
        {
            owner = context.getOwner();
        }

        return repositoryManager.getRepositoryData( repoContext, ObjectType.RawRepo.getId(), owner, true );
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
        return this.context;
    }


    @Override
    public int type()
    {
        return ObjectType.RawRepo.getId();
    }
}
