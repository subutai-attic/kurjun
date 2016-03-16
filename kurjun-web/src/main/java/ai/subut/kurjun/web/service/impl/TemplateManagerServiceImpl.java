package ai.subut.kurjun.web.service.impl;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.repository.UnifiedRepository;
import ai.subut.kurjun.repo.LocalTemplateRepository;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.web.context.ArtifactContext;
import ai.subut.kurjun.web.model.UserContextImpl;
import ai.subut.kurjun.web.service.TemplateManagerService;
import ai.subut.kurjun.web.utils.Utils;
import ninja.Renderable;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.utils.ResponseStreams;


@Singleton
public class TemplateManagerServiceImpl implements TemplateManagerService
{


    private RepositoryFactory repositoryFactory;
    private ArtifactContext artifactContext;
    private LocalTemplateRepository localPublicTemplateRepository;
    private UnifiedRepository unifiedTemplateRepository;


    @Inject
    public TemplateManagerServiceImpl( final RepositoryFactory repositoryFactory,
                                       final ArtifactContext artifactContext )
    {
        this.repositoryFactory = repositoryFactory;
        this.artifactContext = artifactContext;
    }


    @Start( order = 90 )
    public void startService()
    {
        _local();

        _unified();
    }


    @Dispose( order = 90 )
    public void stopService()
    {

    }


    //init local repo
    private void _local()
    {
        this.localPublicTemplateRepository =
                ( LocalTemplateRepository ) repositoryFactory.createLocalTemplate( new KurjunContext( "public" ) );
    }


    private void _unified()
    {
        this.unifiedTemplateRepository = repositoryFactory.createUnifiedRepo();

        if ( localPublicTemplateRepository != null )
        {
            this.unifiedTemplateRepository.getRepositories().add( localPublicTemplateRepository );
        }

        this.unifiedTemplateRepository.getRepositories().addAll( artifactContext.getRemoteTemplateRepositories() );
    }


    @Override
    public SerializableMetadata getTemplate( final byte[] md5 ) throws IOException
    {
        KurjunContext context = artifactContext.getRepository( new BigInteger( 1, md5 ).toString( 16 ) );
        DefaultTemplate defaultTemplate = new DefaultTemplate();
        defaultTemplate.setId( context.getName(), md5 );

        return repositoryFactory.createLocalTemplate( context ).getPackageInfo( defaultTemplate );
    }


    @Override
    public InputStream getTemplateData( final String repository, final byte[] md5, final boolean isKurjunClient )
            throws IOException
    {
        DefaultTemplate defaultTemplate = new DefaultTemplate();
        defaultTemplate.setId( repository, md5 );

        if ( repository.equalsIgnoreCase( "public" ) )
        {
            return unifiedTemplateRepository.getPackageStream( defaultTemplate );
        }
        else
        {
            return repositoryFactory.createLocalTemplate( new KurjunContext( repository ) )
                                    .getPackageStream( defaultTemplate );
        }
    }


    @Override
    public List<SerializableMetadata> list( final String repository, final boolean isKurjunClient ) throws IOException
    {
        if ( repository.equalsIgnoreCase( "public" ) )
        {
            return unifiedTemplateRepository.listPackages();
        }
        else
        {
            return repositoryFactory.createLocalTemplate( new KurjunContext( repository ) ).listPackages();
        }
    }


    @Override
    public boolean isUploadAllowed( final String repository )
    {
        return false;
    }


    @Override
    public String upload( final String repository, final InputStream inputStream ) throws IOException
    {

        SubutaiTemplateMetadata metadata =
                ( SubutaiTemplateMetadata ) getRepo( repository ).put( inputStream, CompressionType.GZIP, repository );

        if ( metadata != null )
        {
            if ( metadata.getMd5Sum() != null )
            {
                artifactContext.store( metadata.getMd5Sum(), new UserContextImpl( repository ) );
            }
        }

        return toId( metadata != null ? metadata.getMd5Sum() : new byte[0], repository );
    }


    @Override
    public String upload( final String repository, final File file ) throws IOException
    {
        SubutaiTemplateMetadata metadata =
                ( SubutaiTemplateMetadata ) getRepo( repository ).put( file, CompressionType.GZIP, repository );

        if ( metadata != null )
        {
            if ( metadata.getMd5Sum() != null )
            {
                artifactContext.store( metadata.getMd5Sum(), new UserContextImpl( repository ) );
            }
        }

        return toId( metadata != null ? metadata.getMd5Sum() : new byte[0], repository );
    }


    @Override
    public boolean delete( String md5, String repository ) throws IOException
    {
        return getRepo( repository ).delete( repository + "." + md5, Utils.MD5.toByteArray( md5 ) );
    }


    @Override
    public LocalRepository createUserRepository( final KurjunContext userName )
    {
        return repositoryFactory.createLocalTemplate( userName );
    }


    @Override
    public void shareTemplate( final String templateId, final String targetUserName )
    {

    }


    @Override
    public Renderable renderableTemplate( final String repository, String md5, final boolean isKurjunClient )
            throws IOException
    {
        LocalRepository publicRepository = getRepo( repository );

        DefaultTemplate defaultTemplate = new DefaultTemplate();

        defaultTemplate.setId( repository, Utils.MD5.toByteArray( md5 ) );

        DefaultTemplate metadata = ( DefaultTemplate ) publicRepository.getPackageInfo( defaultTemplate );

        InputStream inputStream = getTemplateData( repository, Utils.MD5.toByteArray( md5 ), false );

        if ( inputStream != null )
        {
            return ( context, result ) -> {

                result.addHeader( "Content-Disposition", "attachment;filename=" + makeTemplateName( metadata ) );
                result.addHeader( "Contenty-Type", "application/octet-stream" );
                result.addHeader( "Content-Length", String.valueOf( defaultTemplate.getSize() ) );

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
        return null;
    }


    @Override
    public String md5()
    {
        return Utils.MD5.toString( getPublicRepository().md5() );
    }


    @Override
    public List<Map<String, Object>> getSharedTemplateInfos( final byte[] md5, final String templateOwner )
            throws IOException
    {
        return null;
    }


    @Override
    public List<Map<String, Object>> listAsSimple( final String repository ) throws IOException
    {

        return null;
    }


    @Override
    public List<SerializableMetadata> list()
    {
        return null;
    }


    @Override
    public List<Map<String, Object>> getRemoteRepoUrls()
    {
        return null;
    }


    @Override
    public void addRemoteRepository( final URL url, final String token )
    {

    }


    @Override
    public void removeRemoteRepository( final URL url )
    {

    }


    @Override
    public void unshareTemplate( final String templateId, final String targetUserName )
    {

    }


    @Override
    public Set<String> getRepositories()
    {
        return null;
    }


    private String toId( final byte[] md5, String repo )
    {
        String hash = new BigInteger( 1, Arrays.copyOf( md5, md5.length ) ).toString( 16 );

        return repo + "." + hash;
    }


    private LocalRepository getRepo( String repo )
    {
        return repositoryFactory.createLocalTemplate( new KurjunContext( repo ) );
    }


    private LocalRepository getPublicRepository()
    {
        return repositoryFactory.createLocalTemplate( new KurjunContext( "public" ) );
    }


    private String makeTemplateName( SerializableMetadata metadata )
    {
        return metadata.getName() + "_" + metadata.getVersion() + ".tar.gz";
    }
}
