package ai.subut.kurjun.repo.cache;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.MetadataCache;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.Repository;


public class MetadataCacheImpl implements MetadataCache
{

    private final Repository repository;

    private volatile boolean inited;
    private final List<SerializableMetadata> metadata = new ArrayList<>();

    private final Lock lock = new ReentrantLock();


    /**
     * Constructs meta data cache for the supplied repository.
     *
     * @param repository repository whose packages metadata will be cached
     */
    MetadataCacheImpl( Repository repository )
    {
        this.repository = repository;
    }


    @Override
    public List<SerializableMetadata> getMetadataList()
    {
        if ( !inited )
        {
            refresh();
        }
        return Collections.unmodifiableList( metadata );
    }


    @Override
    public SerializableMetadata get( String md5 )
    {
        Objects.requireNonNull( md5, "MD5 checksum" );

        if ( !inited )
        {
            refresh();
        }
        for ( SerializableMetadata m : metadata )
        {
            if ( m.getMd5Sum().equalsIgnoreCase( md5 ) )
            {
                return m;
            }
        }
        return null;
    }


    @Override
    public SerializableMetadata get( String name, String version )
    {
        Objects.requireNonNull( name, "Name in meta data" );

        if ( !inited )
        {
            refresh();
        }
        if ( version != null )
        {
            for ( SerializableMetadata m : metadata )
            {
                if ( name.equals( m.getName() ) && version.equals( m.getVersion() ) )
                {
                    return m;
                }
            }
        }
        else
        {
            Comparator<Metadata> cmp = Collections.reverseOrder( MetadataUtils.makeVersionComparator() );
            Object[] arr = metadata.stream().filter( m -> m.getName().equals( name ) ).sorted( cmp ).toArray();
            if ( arr.length > 0 )
            {
                return ( SerializableMetadata ) arr[0];
            }
        }
        return null;
    }


    @Override
    public void refresh()
    {
        List<SerializableMetadata> items = repository.listPackages();

        lock.lock();
        try
        {
            metadata.clear();
            metadata.addAll( items );
            inited = true;
        }
        finally
        {
            lock.unlock();
        }
    }
}

