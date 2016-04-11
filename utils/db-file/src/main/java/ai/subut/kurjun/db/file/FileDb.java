package ai.subut.kurjun.db.file;


import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * File based db. This db stores records in a key value pairs.
 *
 */
public class FileDb implements Closeable
{
    private final Map<String, Map<Object,?>> mapOfMap = new ConcurrentHashMap<>( 10 );


    public FileDb( String dbFile ) throws IOException
    {
    }


    public synchronized boolean contains( final String mapName, final Object key )
    {
        if ( !mapOfMap.containsKey( mapName ) ) {
            return false;
        }
        return mapOfMap.get( mapName ).containsKey( key );
    }



    public synchronized <T> T get( String mapName, Object key, Class<T> clazz ) {
        if ( mapOfMap.containsKey( mapName ) )
        {
            return (T) mapOfMap.get( mapName ).get( key );
        }

        return null;
    }



    public synchronized <K, V> Map<K, V> get( String mapName )
    {
        Map<K,V> copyMe = (Map<K,V>) mapOfMap.get( mapName );

        if ( copyMe == null )
        {
            return null;
        }

        Map<K, V> result = new HashMap<>( copyMe.size() );
        result.putAll( copyMe );
        return result;
    }


    public synchronized <T> T put( String mapName, Object key, T value )
    {
        Map<Object,T> map = (Map<Object,T>) mapOfMap.get( mapName );

        if ( map != null )
        {
            return map.put( key, value );
        }

        map = new HashMap<>(  );
        mapOfMap.put( mapName, map );
        map.put( key, value );
        return null;
    }



    public synchronized <T> T remove( String mapName, Object key )
    {
        Map<Object,T> map = (Map<Object,T>) mapOfMap.get( mapName );

        if ( map != null )
        {
            return map.remove( key );
        }

        return null;
    }


    @Override
    public void close() throws IOException
    {
    }
}
