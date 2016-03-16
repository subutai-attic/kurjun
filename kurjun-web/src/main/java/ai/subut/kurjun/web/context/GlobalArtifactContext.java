package ai.subut.kurjun.web.context;


import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.Singleton;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.RemoteRepository;
import ai.subut.kurjun.web.model.RepositoryCache;


@Singleton
public class GlobalArtifactContext implements ArtifactContext
{

    private Map<String, KurjunContext> map;

    private Map<String, RepositoryCache> cacheMap;

    private Set<RemoteRepository> remoteTemplate;
    private Set<RemoteRepository> remoteApt;
    private Set<RemoteRepository> remoteRaw;


    public GlobalArtifactContext()
    {
        this.cacheMap = new ConcurrentHashMap<>();
        this.map = new ConcurrentHashMap<>();

        this.remoteTemplate = new HashSet<>();
        this.remoteApt = new HashSet<>();
        this.remoteRaw = new HashSet<>();
    }


    @Override
    public KurjunContext getRepository( final String md5 )
    {
        return map.get( md5 );
    }


    @Override
    public void store( final byte[] md5, final KurjunContext repository )
    {
        String hash = new BigInteger( 1, Arrays.copyOf( md5, md5.length ) ).toString( 16 );
        map.put( hash, repository );
    }


    @Override
    public void remove( final byte[] md5 )
    {

        String hash = new BigInteger( 1, Arrays.copyOf( md5, md5.length ) ).toString( 16 );
        map.remove( hash );
    }


    @Override
    public String getMd5( final String repository )
    {
        RepositoryCache repositoryCache = cacheMap.get( repository );

        return repositoryCache.getMd5();
    }


    @Override
    public void store( final String repository, final RepositoryCache repositoryCache )
    {
        cacheMap.put( repository, repositoryCache );
    }


    @Override
    public List<SerializableMetadata> getList( final String repository )
    {
        return getRepositoryCache( repository ).getMetadataList();
    }


    @Override
    public RepositoryCache getRepositoryCache( final String repository )
    {
        return cacheMap.get( repository );
    }


    @Override
    public void addRemoteTemplateRepository( RemoteRepository remoteRepository )
    {
        this.remoteTemplate.add( remoteRepository );
    }


    @Override
    public Set<RemoteRepository> getRemoteRawRepositories()
    {
        return this.remoteRaw;
    }


    @Override
    public void addRemoteRawRepositories( final RemoteRepository remoteRepository )
    {
        this.remoteRaw.add( remoteRepository );
    }


    @Override
    public Set<RemoteRepository> getRemoteAptRepositories()
    {
        return this.remoteApt;
    }


    @Override
    public void addRemoteAptRepositories( final RemoteRepository remoteRepository )
    {
        this.remoteApt.add( remoteRepository );
    }


    @Override
    public Set<RemoteRepository> getRemoteTemplateRepositories()
    {
        return this.remoteTemplate;
    }

}
