package ai.subut.kurjun.web.service.impl;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;

import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.web.service.TemplateManagerService;


public class TemplateManagerServiceImpl implements TemplateManagerService
{

    @Inject
    RepositoryFactory repositoryFactory;

    @Override
    public DefaultTemplate getTemplate( final String repository, final byte[] md5, final String templateOwner ) throws IOException
    {

        return null;
    }


    @Override
    public DefaultTemplate getTemplate( final String repository, final String name, final String version) throws IOException
    {
        return null;
    }


    @Override
    public DefaultTemplate getTemplate( final String name )
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
        return null;
    }


    @Override
    public boolean delete( final String repository, final String templateOwner, final byte[] md5 ) throws IOException
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
    public void createUserRepository( final String userName )
    {

    }


    @Override
    public void shareTemplate( final String templateId, final String targetUserName )
    {

    }


    @Override
    public void unshareTemplate( final String templateId, final String targetUserName )
    {

    }
}
