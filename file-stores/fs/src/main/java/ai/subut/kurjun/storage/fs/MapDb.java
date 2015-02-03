package ai.subut.kurjun.storage.fs;


import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;


class MapDb implements Closeable
{

    public static final String MAPDB_FILE = "kurjun-mapdb";
    public static final String MAP_NAME = "fileChecksumsMap";

    private DB db;
    private boolean commitOnClose = true;


    public MapDb( Path parent ) throws IOException
    {
        if ( Files.notExists( parent ) )
        {
            Files.createDirectories( parent );
        }
        db = DBMaker.newFileDB( parent.resolve( MAPDB_FILE ).toFile() ).make();
    }


    public boolean isCommitOnClose()
    {
        return commitOnClose;
    }


    public void setCommitOnClose( boolean commitOnClose )
    {
        this.commitOnClose = commitOnClose;
    }


    public HTreeMap<String, String> getMap()
    {
        return db.getHashMap( MAP_NAME );
    }


    public void commit()
    {
        db.commit();
    }


    @Override
    public void close() throws IOException
    {
        if ( commitOnClose )
        {
            commit();
        }
        db.close();
    }

}

