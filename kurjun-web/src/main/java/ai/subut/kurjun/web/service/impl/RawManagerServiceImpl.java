package ai.subut.kurjun.web.service.impl;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.raw.RawMetadata;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.UnifiedRepository;
import ai.subut.kurjun.repo.LocalRawRepository;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.web.context.ArtifactContext;
import ai.subut.kurjun.web.service.RawManagerService;
import ai.subut.kurjun.web.utils.Utils;
import ninja.Renderable;
import ninja.utils.ResponseStreams;

import static com.google.common.base.Preconditions.checkNotNull;


@Singleton
public class RawManagerServiceImpl implements RawManagerService
{
    private RepositoryFactory repositoryFactory;
    private LocalRawRepository localPublicRawRepository;
    private UnifiedRepository unifiedRepository;
    private ArtifactContext artifactContext;


    @Inject
    public RawManagerServiceImpl( final RepositoryFactory repositoryFactory, final ArtifactContext artifactContext )
    {
        this.repositoryFactory = repositoryFactory;
        this.artifactContext = artifactContext;

        _local();
        _unified();
    }


    private void _local()
    {
        this.localPublicRawRepository = this.repositoryFactory.createLocalRaw( new KurjunContext( "raw" ) );
    }


    private void _unified()
    {
        this.unifiedRepository = this.repositoryFactory.createUnifiedRepo();
        unifiedRepository.getRepositories().add( this.localPublicRawRepository );
        unifiedRepository.getRepositories().addAll( artifactContext.getRemoteRawRepositories() );
    }


    @Override
    public Renderable getFile( final String name )
    {
        checkNotNull( name, "Name cannot be empty" );

        DefaultMetadata metadata = new DefaultMetadata();
        metadata.setName( name );

        RawMetadata meta = ( RawMetadata ) this.unifiedRepository.getPackageInfo( metadata );

        if ( meta != null )
        {
            InputStream inputStream = this.unifiedRepository.getPackageStream( meta );

            if ( inputStream != null )
            {
                return ( context, result ) -> {

                    result.addHeader( "Content-Disposition", "attachment;filename=" + meta.getName() );
                    result.addHeader( "Contenty-Type", "application/octet-stream" );
                    result.addHeader( "Content-Length", String.valueOf( meta.getSize() ) );

                    ResponseStreams responseStreams = context.finalizeHeaders( result );

                    try ( OutputStream outputStream = responseStreams.getOutputStream() )
                    {
                        ByteStreams.copy( inputStream, outputStream );
                    }
                    catch ( IOException e )
                    {
                        e.printStackTrace();
                    }
                };
            }
        }
        return null;
    }


    @Override
    public String md5()
    {
        return Utils.MD5.toString( localPublicRawRepository.md5() );
    }


    @Override
    public Renderable getFile( final byte[] md5 )
    {
        checkNotNull( md5, "MD5 cannot be null" );

        DefaultMetadata defaultMetadata = new DefaultMetadata();
        defaultMetadata.setMd5sum( md5 );

        RawMetadata meta = ( RawMetadata ) this.unifiedRepository.getPackageInfo( defaultMetadata );

        if ( meta != null )
        {
            InputStream inputStream = this.unifiedRepository.getPackageStream( meta );
            if ( inputStream != null )
            {
                return ( context, result ) -> {

                    result.addHeader( "Content-Disposition", "attachment;filename=" + meta.getName() );
                    result.addHeader( "Contenty-Type", "application/octet-stream" );
                    result.addHeader( "Content-Length", String.valueOf( meta.getSize() ) );

                    ResponseStreams responseStreams = context.finalizeHeaders( result );

                    try ( OutputStream outputStream = responseStreams.getOutputStream() )
                    {
                        ByteStreams.copy( inputStream, outputStream );
                    }
                    catch ( IOException e )
                    {
                        e.printStackTrace();
                    }
                };
            }
        }

        return null;
    }


    @Override
    public Renderable getFile( final byte[] md5, final boolean isKurjun )
    {
        return getFile( md5 );
    }


    @Override
    public boolean delete( final byte[] md5 )
    {
        try
        {
            return localPublicRawRepository.delete( md5 );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public SerializableMetadata getInfo( final byte[] md5 )
    {
        DefaultMetadata metadata = new DefaultMetadata();
        metadata.setMd5sum( md5 );

        return unifiedRepository.getPackageInfo( metadata );
    }


    @Override
    public Metadata put( final File file )
    {
        Metadata metadata = null;
        try
        {
            metadata = localPublicRawRepository.put( new FileInputStream( file ), file.getName() );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return metadata;
    }


    @Override
    public Metadata put( final File file, final String repository )
    {
        Metadata metadata = null;
        try
        {
            metadata = localPublicRawRepository.put( new FileInputStream( file ), CompressionType.NONE, repository );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return metadata;
    }


    @Override
    public List<SerializableMetadata> list()
    {
        return unifiedRepository.listPackages();
    }
}
