package ai.subut.kurjun.db.file;


import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;
import org.mapdb.TxMaker;


/**
 * File based db. This db stores records in a key value pairs.
 *
 */
public class FileDb implements Closeable
{
    private final Object lock = new Object();
    private final File file;
    protected final TxMaker txMaker;


    /**
     * Constructs file based db backed by supplied file.
     *
     * @param dbFile the path to the db file to use
     * @throws IOException
     */
    public FileDb( String dbFile ) throws IOException
    {
        this( dbFile, false );
    }


    /**
     * Executes using the current thread context, and saving then restoring it after
     * execution.
     *
     * @param func the transactional function block to use
     * @param <T> the data type to take or return from the function
     * @return the data type to get, replace or remove from the map
     */
    private <T> T threadLocalExecute( Fun.Function1<T, DB> func )
    {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );

        /*
         * Incredibly high costs are incurred when rolling back out of transactions. By
         * serializing we actually perform a lot better: approximate by 3-4x when
         * concurrency starts to increase past 3-4 threads. The best thing to do is to
         * just serialize access to the db and prevent rollbacks all together.
         *
         * Soon I will take a different strategy based on how reads work in this picture.
         * I do not think they need to be syncrhonized however this must be tested
         * thoroughly before making any presumptions.
         */
        try
        {
            synchronized ( lock )
            {
                return txMaker.execute( func );
            }
        }
        finally
        {
            synchronized ( lock )
            {
                lock.notifyAll();
            }
            Thread.currentThread().setContextClassLoader( tccl );
        }
    }


    FileDb( String dbFile, boolean readOnly ) throws IOException
    {
        if ( dbFile == null || dbFile.isEmpty() )
        {
            throw new IllegalArgumentException( "File db path can not be empty" );
        }

        Path path = Paths.get( dbFile );
        // ensure parent dirs do exist
        Files.createDirectories( path.getParent() );
        this.file = path.toFile();

        DBMaker dbMaker = DBMaker.newFileDB( file );

        if ( readOnly )
        {
            dbMaker.readOnly();
        }

        // TODO: Check on standalone env of temporary CL swapping
        // In newer version there is a setter for CL. See: https://github.com/jankotek/mapdb/issues/555
        // By using this setter CL swapping can be avoided.

        ClassLoader tccl = Thread.currentThread().getContextClassLoader();

        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );

            this.txMaker = dbMaker
                    .closeOnJvmShutdown()
                    .mmapFileEnableIfSupported()
                    .snapshotEnable()
                    .makeTxMaker();
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( tccl );
        }
    }


    /**
     * Gets underlying db file.
     *
     * @return file to underlying db file
     */
    public File getFile()
    {
        return file;
    }


    /**
     * Checks if association exists for the key in a map with supplied name.
     *
     * @param mapName name of the map to check
     * @param key key to check association for
     * @return {@code true} if map contains association for the key; {@code false} otherwise
     */
    public synchronized boolean contains( final String mapName, final Object key )
    {
        return threadLocalExecute( new Fun.Function1<Boolean, DB>() {
            @Override
            public Boolean run( final DB db ) {
                return checkNameExists( mapName, db ) && db.getHashMap( mapName ).containsKey( key );
            }
        } );
    }


    /**
     * Gets value for the key in a map with supplied name.
     *
     * @param <T> type of the value
     * @param mapName name of the map to get value from
     * @param key the key to look for
     * @param clazz type of the returned value
     * @return value mapped to supplied key; {@code null} if no value is mapped
     */
    public synchronized <T> T get( String mapName, Object key, Class<T> clazz )
    {
        return threadLocalExecute( new Fun.Function1<T, DB>() {
            @Override
            public T run( final DB db ) {
                return ( T ) db.getHashMap( mapName ).get( key );
            }
        } );
    }


    /**
     * Gets a readonly snapshot view of the map with supplied name.
     *
     * @param <K> type of map keys
     * @param <V> type of map values
     * @param mapName name of the map to get
     * @return readonly view of the map
     */
    public synchronized <K, V> Map<K, V> get( String mapName )
    {
        return threadLocalExecute( new Fun.Function1<Map<K,V>, DB>() {
            @Override
            public Map<K, V> run( final DB db ) {
                Map<K, V> result = new HashMap<>();
                if ( checkNameExists( mapName, db ) )
                {
                    Map<K, V> snapshot = ( Map<K, V> ) db.getHashMap( mapName ).snapshot();
                    result.putAll( snapshot );
                }
                return result;
            }
        } );
    }


    /**
     * Associate the key to the given value in a map with supplied name.
     *
     * @param <T> type of the value
     * @param mapName name of the map to put mapping to
     * @param key key value
     * @param value value to be associated with the key
     * @return the previous value associated with key, or null if there was no mapping for key
     */
    public synchronized <T> T put( String mapName, Object key, T value )
    {
        return threadLocalExecute( new Fun.Function1<T, DB>() {
            @Override
            public T run( final DB db )
            {
                Map<Object, T> map = db.getHashMap( mapName );
                return map.put( key, value );
            }
        });
    }


    /**
     * Removes mapping for the key in a map with supplied name.
     *
     * @param <T> type of the value
     * @param mapName map name
     * @param key key value to remove mapping for
     * @return the previous value associated with key, or null if there was no mapping for key
     */
    public synchronized <T> T remove( String mapName, Object key )
    {
        return threadLocalExecute( new Fun.Function1<T, DB>() {
            @Override
            public T run( final DB db ) {
                Map<Object, T> map = db.getHashMap( mapName );
                return map.remove( key );
            }
        } );
    }


    @Override
    public void close() throws IOException
    {
        if ( txMaker != null )
        {
            txMaker.close();
        }
    }


    /**
     * Checks if a collection with supplied name exists in the store.
     * <p>
     * This method is particularly useful for methods that do not introduce changes, e.g. get and contains methods. In
     * methods like these we can check if collection exists and do further actions. This avoids empty commits in cases
     * where collection did not exist in store and getHashMap method created one.
     * <p>
     * Empty commits may be the reason for issues mentioned in https://github.com/jankotek/mapdb/issues/509
     *
     *
     * @param name name of the collection
     * @param db store to check
     * @return {@code true} if the name is already used to create a collection; {@code false} otherwise
     */
    private boolean checkNameExists( String name, DB db )
    {
        try
        {
            // this method is preferred to others like 'exists()' because it does not use locks
            db.checkNameNotExists( name );
            return false;
        }
        catch ( IllegalArgumentException ex )
        {
            return true;
        }
    }
}

