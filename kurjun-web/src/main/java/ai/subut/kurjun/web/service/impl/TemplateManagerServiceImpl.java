package ai.subut.kurjun.web.service.impl;


import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.repo.LocalTemplateRepository;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.security.UserContextImpl;
import ai.subut.kurjun.web.service.TemplateManagerService;


public class TemplateManagerServiceImpl implements TemplateManagerService
{

    @Inject
    RepositoryFactory repositoryFactory;

    @Inject
    LocalTemplateRepository localTemplateRepository;


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
    public InputStream getTemplateData( final String repository, final byte[] md5, final String templateOwner,
                                        final boolean isKurjunClient ) throws IOException
    {
        return null;
    }


    @Override
    public List<DefaultTemplate> list( final String repository, final boolean isKurjunClient ) throws IOException
    {
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
    public List<DefaultTemplate> list()
    {
        return null;
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
    public boolean delete( final byte[] md5 ) throws IOException
    {
        return false;
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


    private String toId( final byte[] md5, String repo )
    {
        String hash = new BigInteger( 1, Arrays.copyOf( md5, md5.length ) ).toString( 16 );

        return repo + "." + hash;
    }
}
