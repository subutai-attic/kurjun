package ai.subut.kurjun.db.file;


import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.TxMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;


/**
 * File based db. This db stores records in a key value pairs.
 *
 */
public class FileDb implements Closeable
{
    private static final Logger LOGGER = LoggerFactory.getLogger( FileDb.class );

    protected final TxMaker txMaker;


    /**
     * Constructs file based db backed by supplied file. File path argument is annotated with {@link Named} to enable
     * file path injection. This constructor has fail-safe handling of {@code null} values: file db initialized in
     * system temporary directory. This will work until the file is cleared from temp directory.
     *
     * @param dbFile
     * @throws IOException
     */
    @Inject
    public FileDb( @Named( FileDbModule.DB_FILE_PATH ) String dbFile ) throws IOException
    {
        this( dbFile, false );
    }


    FileDb( String dbFile, boolean readOnly ) throws IOException
    {
        DBMaker dbMaker;
        if ( dbFile != null )
        {
            Path path = Paths.get( dbFile );
            if ( Files.notExists( path ) )
            {
                Files.createDirectories( path.getParent() );
            }
            dbMaker = DBMaker.newFileDB( path.toFile() );
        }
        else
        {
            LOGGER.warn( "DB file not supplied. Using temporary file!!!" );
            dbMaker = DBMaker.newTempFileDB();
        }

        if ( readOnly )
        {
            dbMaker.readOnly();
        }
        this.txMaker = dbMaker
                .closeOnJvmShutdown()
                .mmapFileEnableIfSupported()
                .makeTxMaker();
    }


    /**
     * Checks if association exists for the key in a map with supplied name.
     *
     * @param mapName name of the map to check
     * @param key key to check association for
     * @return {@code true} if map contains association for the key; {@code false} otherwise
     */
    public boolean contains( String mapName, Object key )
    {
        DB db = txMaker.makeTx();
        try
        {
            return db.getHashMap( mapName ).containsKey( key );
        }
        finally
        {
            db.close();
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
        DB db = txMaker.makeTx();
        try
        {
            return ( T ) db.getHashMap( mapName ).get( key );
        }
        finally
        {
            db.close();
        }
    }


    /**
     * Associated the key to the given value in a map with supplied name.
     *
     * @param <T>
     * @param mapName name of the map to put mapping to
     * @param key
     * @param value
     * @return the previous value associated with key, or null if there was no mapping for key
     */
    public <T> T put( String mapName, Object key, T value )
    {
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


    /**
     * Removes mapping for the key in a map with supplied name.
     *
     * @param <T> type of the value
     * @param mapName map name
     * @param key key value to remove mapping for
     * @return the previous value associated with key, or null if there was no mapping for key
     */
    public <T> T remove( String mapName, Object key )
    {
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


    @Override
    public void close() throws IOException
    {
        if ( txMaker != null )
        {
            txMaker.close();
        }
    }

}

