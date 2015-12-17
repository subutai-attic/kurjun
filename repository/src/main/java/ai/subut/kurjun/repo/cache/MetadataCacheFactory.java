package ai.subut.kurjun.repo.cache;


import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.inject.Singleton;

import ai.subut.kurjun.model.metadata.MetadataCache;
import ai.subut.kurjun.model.repository.Repository;


@Singleton
public class MetadataCacheFactory
{

    private ConcurrentMap<String, MetadataCache> caches = new ConcurrentHashMap<>();


    public MetadataCache get( Repository repository )
    {
        String key = makeKey( repository );
        MetadataCache newCache = new MetadataCacheImpl( repository );

        MetadataCache existing = caches.putIfAbsent( key, newCache );
        return existing != null ? existing : newCache;
    }


    private String makeKey( Repository repository )
    {
        URL url = repository.getUrl();
        if ( url != null )
        {
            return url.toString();
        }
        throw new IllegalArgumentException( "Supplied repository does have its URL set." );
    }
}

