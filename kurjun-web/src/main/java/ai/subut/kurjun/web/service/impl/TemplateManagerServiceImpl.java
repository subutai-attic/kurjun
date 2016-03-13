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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.metadata.common.subutai.TemplateId;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.web.context.ArtifactContext;
import ai.subut.kurjun.web.model.UserContextImpl;
import ai.subut.kurjun.web.service.TemplateManagerService;
import ninja.Renderable;
import ninja.utils.ResponseStreams;


@Singleton
public class TemplateManagerServiceImpl implements TemplateManagerService
{


    private RepositoryFactory repositoryFactory;
    private ArtifactContext artifactContext;


    @Inject
    public TemplateManagerServiceImpl( final RepositoryFactory repositoryFactory,
                                       final ArtifactContext artifactContext )
    {
        this.repositoryFactory = repositoryFactory;
        this.artifactContext = artifactContext;
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
        LocalRepository localRepository = repositoryFactory.createLocalTemplate( new UserContextImpl( repository ) );

        return localRepository.getPackageStream( defaultTemplate );
    }


    @Override
    public List<SerializableMetadata> list( final String repository, final boolean isKurjunClient ) throws IOException
    {
        UserContextImpl userContext = new UserContextImpl( repository );

        return repositoryFactory.createLocalTemplate( userContext ).listPackages();
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
    public boolean delete( String md5 ) throws IOException
    {
        KurjunContext user = artifactContext.getRepository( md5 );
        TemplateId templateId = new TemplateId( user.getName(), md5 );

        return getRepo( md5 ).delete( templateId, decodeMd5( md5 ) );
    }


    @Override
    public LocalRepository createUserRepository( final KurjunContext userName )
    {
        return repositoryFactory.createLocalTemplate( userName );
    }


    @Override
    public Renderable renderableTemplate( final String repository, String md5, final boolean isKurjunClient )
            throws IOException
    {

        InputStream inputStream = getTemplateData( repository, decodeMd5( md5 ), false );

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
        return null;
    }


    @Override
    public void shareTemplate( final String templateId, final String targetUserName )
    {

    }


    @Override
    public boolean isUploadAllowed( final String repository )
    {
        return false;
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
    public String repositoryMd5()
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


    protected byte[] decodeMd5( String md5 )
    {
        if ( md5 != null )
        {
            try
            {
                return Hex.decodeHex( md5.toCharArray() );
            }
            catch ( DecoderException ex )
            {
                ex.printStackTrace();
            }
        }
        return null;
    }


    private LocalRepository getRepo( String repo )
    {
        return repositoryFactory.createLocalTemplate( new UserContextImpl( repo ) );
    }
}
