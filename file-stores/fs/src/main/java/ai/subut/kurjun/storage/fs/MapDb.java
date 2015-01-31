/*
 * Copyright 2015 azilet.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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


    public HTreeMap<byte[], String> getMap()
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

