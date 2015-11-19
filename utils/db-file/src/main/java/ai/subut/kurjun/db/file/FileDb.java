package ai.subut.kurjun.db.file;


import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.TxMaker;


/**
 * File based db. This db stores records in a key value pairs.
 *
 */
public class FileDb implements Closeable
{

    private final File file;
    protected final TxMaker txMaker;



    /**
     * Constructs file based db backed by supplied file.
     *
     * @param dbFile
     * @throws IOException
     */
    public FileDb( String dbFile ) throws IOException
    {
        this( dbFile, false );
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
     * @return {@code true} if map contains association for the key;
     * {@code false} otherwise
     */
    public boolean contains( String mapName, Object key )
    {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );

            DB db = txMaker.makeTx();
            try
            {
                return db.getHashMap( mapName ).containsKey( key );
            }
            finally
            {
                db.commit();
                db.close();
            }
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( tccl );
        }
    }


    /**
     * Gets value for the key in a map with supplied name.
     *
     * @param <T> type of the value
     * @param mapName name of the map to get value from
     * @param key the key to look for
     * @param clazz type of the returned value
     * @return
     */
    public <T> T get( String mapName, Object key, Class<T> clazz )
    {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );

            DB db = txMaker.makeTx();
            try
            {
                return ( T ) db.getHashMap( mapName ).get( key );
            }
            finally
            {
                db.commit();
                db.close();
            }
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( tccl );
        }
    }


    /**
     * Gets a readonly snapshot view of the map with supplied name.
     *
     * @param <K> type of map keys
     * @param <V> type of map values
     * @param mapName name of the map to get
     * @return readonly view of the map
     */
    public <K, V> Map<K, V> get( String mapName )
    {
        // this call is to avoid "IllegalAccessError: Can not create snapshot with uncommited data"
        // it occurs when there was no map with given name and tried to get a snapshot
        contains( mapName, "dummy-key" );

        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );

            Map<K, V> result = new HashMap<>();
            DB db = txMaker.makeTx();
            try
            {
                Map<K, V> snapshot = ( Map<K, V> ) db.getHashMap( mapName ).snapshot();
                result.putAll( snapshot );
            }
            finally
            {
                db.commit();
                db.close();
            }
            return Collections.unmodifiableMap( result );
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( tccl );
        }
    }


    /**
     * Associated the key to the given value in a map with supplied name.
     *
     * @param <T> type of the value
     * @param mapName name of the map to put mapping to
     * @param key key value
     * @param value value to be associated with the key
     * @return the previous value associated with key, or null if there was no
     * mapping for key
     */
    public <T> T put( String mapName, Object key, T value )
    {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );

            DB db = txMaker.makeTx();
            try
            {
                T put = ( T ) db.getHashMap( mapName ).put( key, value );
                db.commit();
                return put;
            }
            finally
            {
                db.close();
            }
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( tccl );
        }
    }


    /**
     * Removes mapping for the key in a map with supplied name.
     *
     * @param <T> type of the value
     * @param mapName map name
     * @param key key value to remove mapping for
     * @return the previous value associated with key, or null if there was no
     * mapping for key
     */
    public <T> T remove( String mapName, Object key )
    {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
            DB db = txMaker.makeTx();
            try
            {
                T removed = ( T ) db.getHashMap( mapName ).remove( key );
                db.commit();
                return removed;
            }
            finally
            {
                db.close();
            }
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( tccl );
        }
    }


    @Override
    public void close() throws IOException
    {
        if ( txMaker != null )
        {
            txMaker.close();
        }
    }

}
