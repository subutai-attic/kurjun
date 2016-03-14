package ai.subut.kurjun.web.service.impl;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.raw.RawMetadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.UnifiedRepository;
import ai.subut.kurjun.repo.LocalRawRepository;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.web.service.RawManagerService;
import ninja.Renderable;
import ninja.utils.ResponseStreams;

import static com.google.common.base.Preconditions.checkNotNull;


public class RawManagerServiceImpl implements RawManagerService
{
    private RepositoryFactory repositoryFactory;
    private LocalRawRepository localRawRepository;
    private UnifiedRepository unifiedRepository;


    @Inject
    public RawManagerServiceImpl( final RepositoryFactory repositoryFactory )
    {
        this.repositoryFactory = repositoryFactory;
        _local();
    }


    private void _local()
    {
        this.localRawRepository = this.repositoryFactory.createLocalRaw( new KurjunContext( "raw" ) );
        this.unifiedRepository = this.repositoryFactory.createUnifiedRepo();
        unifiedRepository.getRepositories().add( this.localRawRepository );
    }


    private LocalRawRepository getUserRawRepository( KurjunContext kurjunContext )
    {
        return this.repositoryFactory.createLocalRaw( kurjunContext );
    }


    private LocalRawRepository getUserRawRepository( String repository )
    {
        return this.repositoryFactory.createLocalRaw( new KurjunContext( repository ) );
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
        return false;
    }


    @Override
    public Renderable getFile( final String name )
    {
        return null;
    }


    @Override
    public SerializableMetadata getInfo( final byte[] md5 )
    {
        return null;
    }


    @Override
    public boolean put( final File file )
    {
        return false;
    }


    @Override
    public boolean put( final File file, final String repository )
    {

        return false;
    }


    @Override
    public List<SerializableMetadata> list()
    {
        return unifiedRepository.listPackages();
    }
}
