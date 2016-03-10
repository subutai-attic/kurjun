package ai.subut.kurjun.web.service.impl;


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
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.metadata.common.subutai.TemplateId;
import ai.subut.kurjun.model.context.ArtifactContext;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.user.UserContext;
import ai.subut.kurjun.repo.LocalTemplateRepository;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.security.UserContextImpl;
import ai.subut.kurjun.web.service.TemplateManagerService;
import ninja.Renderable;
import ninja.utils.ResponseStreams;

@Singleton
public class TemplateManagerServiceImpl implements TemplateManagerService
{

    @Inject
    RepositoryFactory repositoryFactory;

    @Inject
    LocalTemplateRepository localTemplateRepository;

    @Inject
    ArtifactContext artifactContext;

    @Inject
    KurjunProperties kurjunProperties;


    @Override
    public DefaultTemplate getTemplate( final byte[] md5 ) throws IOException
    {

        return null;
    }


    @Override
    public List<Map<String, Object>> getRemoteRepoUrls()
    {
        return null;
    }


    @Override
    public InputStream getTemplateData( final String repository, final byte[] md5, final boolean isKurjunClient )
            throws IOException
    {
        DefaultTemplate defaultTemplate = new DefaultTemplate();
        defaultTemplate.setId( repository, md5 );

        return localTemplateRepository.getPackageStream( defaultTemplate );
    }


    @Override
    public List<DefaultTemplate> list( final String repository, final boolean isKurjunClient ) throws IOException
    {
        UserContextImpl userContext = new UserContextImpl( repository );

        localTemplateRepository.listPackages();
        return null;
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
        return localTemplateRepository.listPackages();
    }


    @Override
    public boolean isUploadAllowed( final String repository )
    {
        return false;
    }


    @Override
    public String upload( final String repository, final InputStream inputStream ) throws IOException
    {

        SubutaiTemplateMetadata metadata = ( SubutaiTemplateMetadata ) localTemplateRepository
                .put( inputStream, CompressionType.GZIP, repository );

        if ( metadata != null )
        {
            if ( metadata.getMd5Sum() != null )
            {
                localTemplateRepository.index( metadata, new UserContextImpl( repository ) );
            }
        }

        return toId( metadata != null ? metadata.getMd5Sum() : new byte[0], repository );
    }


    @Override
    public boolean delete( String md5 ) throws IOException
    {
        UserContext user = artifactContext.getRepository( md5 );
        TemplateId templateId = new TemplateId( user.getFingerprint(), md5 );

        return localTemplateRepository.delete( templateId, decodeMd5( md5 ) );
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
    public Set<String> getRepositories()
    {
        return null;
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
    public void unshareTemplate( final String templateId, final String targetUserName )
    {

    }


    @Override
    public Renderable renderableTemplate( final String repository, String md5, final boolean isKurjunClient )
            throws IOException
    {

        InputStream inputStream = getTemplateData( repository, decodeMd5( md5 ), false );

        Renderable renderable = ( context1, result ) -> {

            ResponseStreams responseStreams = context1.finalizeHeaders( result );

            try ( OutputStream outputStream = responseStreams.getOutputStream() )
            {
                ByteStreams.copy( inputStream, outputStream );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        };

        return renderable;
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
}
