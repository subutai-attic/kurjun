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
package ai.subut.kurjun.metadata.storage.file;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.codec.binary.Hex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ai.subut.kurjun.metadata.common.PackageMetadataImpl;
import ai.subut.kurjun.model.metadata.PackageMetadata;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;


public class DbFilePackageMetadataStore implements PackageMetadataStore
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    Path location;


    public DbFilePackageMetadataStore( String location )
    {
        this.location = Paths.get( location );
    }


    @Override
    public boolean contains( byte[] md5 ) throws IOException
    {
        try ( MapDb db = new MapDb( location ) )
        {
            return db.getMap().containsKey( Hex.encodeHexString( md5 ) );
        }
    }


    @Override
    public PackageMetadata get( byte[] md5 ) throws IOException
    {
        try ( MapDb db = new MapDb( location ) )
        {
            String metadata = db.getMap().get( Hex.encodeHexString( md5 ) );
            return GSON.fromJson( metadata, PackageMetadataImpl.class );
        }
    }


    @Override
    public boolean put( PackageMetadata meta ) throws IOException
    {
        String hex = Hex.encodeHexString( meta.getMd5Sum() );
        try ( MapDb db = new MapDb( location ) )
        {
            return db.getMap().putIfAbsent( hex, GSON.toJson( meta ) ) == null;
        }
    }


    @Override
    public boolean remove( byte[] md5 ) throws IOException
    {
        try ( MapDb db = new MapDb( location ) )
        {
            return db.getMap().remove( Hex.encodeHexString( md5 ) ) != null;
        }
    }

}

