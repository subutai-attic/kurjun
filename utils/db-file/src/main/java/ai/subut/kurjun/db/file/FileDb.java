package ai.subut.kurjun.db.file;


import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

import ai.subut.kurjun.metadata.common.utils.MetadataUtils;


/**
 * File based db. This db stores records in a key value pairs.
 */
public class FileDb implements Closeable
{
    private static final Map<String, Map<Object, ?>> mapOfMap = new ConcurrentHashMap<>( 10 );

    Gson gson = new Gson();

    private static final String ROOT_DIR = "/tmp/var/kurjun/";

    volatile boolean loaded = false;


    public FileDb( String dbFile ) throws IOException
    {
        createDir( ROOT_DIR );
    }


    public synchronized boolean contains( final String mapName, final Object key )
    {
        if ( !mapOfMap.containsKey( mapName ) )
        {
            return false;
        }
        return mapOfMap.get( mapName ).containsKey( key );
    }


    public synchronized <T> T get( String mapName, Object key, Class<T> clazz )
    {
        if ( mapOfMap.containsKey( mapName ) )
        {
            return ( T ) mapOfMap.get( mapName ).get( key );
        }

        return null;
    }


    public synchronized <K, V> Map<K, V> get( String mapName )
    {
        Map<K, V> copyMe = ( Map<K, V> ) mapOfMap.get( mapName );

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
        Map<Object, T> map = ( Map<Object, T> ) mapOfMap.get( mapName );

        if ( map != null )
        {
            return map.put( key, value );
        }

        map = new HashMap<>();

        mapOfMap.put( mapName, map );
        //write to fs
        persist( mapName, key, value );

        return map.put( key, value );
    }


    public synchronized <T> T remove( String mapName, Object key )
    {
        Map<Object, T> map = ( Map<Object, T> ) mapOfMap.get( mapName );

        if ( map != null )
        {
            try
            {
                removeJsonFile( mapName, key );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
            return map.remove( key );
        }

        return null;
    }


    private synchronized void removeJsonFile( String mapName, Object key ) throws IOException
    {
        String targetDir = ROOT_DIR + mapName + String.valueOf( key ) + ".json";

        Path path = new File( targetDir ).toPath();

        Files.deleteIfExists( path );
    }


    @Override
    public synchronized void close() throws IOException
    {

    }


    //persist replacing previous file
    private synchronized void persist( String mapName, Object key, Object value )
    {
        String targetDir = ROOT_DIR + mapName;

        //create dir if does not exist
        if ( createDir( targetDir ) )
        {
            try
            {
                Path targetPath = new File( targetDir ).toPath();

                Path tmpPath = Files.createTempFile( String.valueOf( key ), ".json" );

                FileOutputStream fileOutputStream = new FileOutputStream( tmpPath.toFile() );

                fileOutputStream.write( MetadataUtils.JSON.toJson( value ).getBytes() );

                Files.move( tmpPath, targetPath, StandardCopyOption.REPLACE_EXISTING );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }


    /**
     * Flush flashes content of the Map of Maps to FS
     *
     * @return success on successful writes, false otherwise
     */
    private void flush()
    {
    }


    /**
     * Creates dir if does not exist
     *
     * @param mapName - dir name
     *
     * @return true if dir exists or created successfully, false otherwise
     */
    private synchronized boolean createDir( String mapName )
    {
        return new File( mapName ).exists() || new File( mapName ).mkdirs();
    }


    private synchronized void loadMapOfMaps() throws IOException
    {
        File fileDirectory = new File( ROOT_DIR );

        File[] files = fileDirectory.listFiles();

        if ( files != null )
        {
            //for files in root dir
            for ( File file : files )
            {
                //if it is a dir, search of .json files
                if ( file.isDirectory() )
                {
                    //get all json files in that dir
                    File jsonFiles[] = file.listFiles( filenameFilter() );

                    Map map = new HashMap<>();
                    //for each json file convert back to type
                    for ( File jsonFile : jsonFiles )
                    {
                        FileInputStream fileInputStream = new FileInputStream( jsonFile );
                        byte[] data = new byte[( int ) jsonFile.length()];
                        fileInputStream.read( data );
                        fileInputStream.close();
                        String str = new String( data, "UTF-8" );
                        //                    map.put( jsonFile.getName(), gson.fromJson( str ) );
                    }
                }
            }
        }
    }


    private FilenameFilter filenameFilter()
    {
        FilenameFilter filter = ( dir, name ) -> name.endsWith( ".json" );
        return filter;
    }
}
