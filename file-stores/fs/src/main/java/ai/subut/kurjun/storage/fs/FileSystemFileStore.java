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


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Hex;

import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.storage.fs.util.FileHelpers;


/**
 * File store backed by a local file system. Mappings of files checksums to their location paths are saved in an
 * embedded database. Actual files are saved under subdirectories of a common parent root directory specified in
 * constructor. Subdirectories are a one-letter directories that correspond to the first letter of files checksums in
 * hex format.
 */
public class FileSystemFileStore implements FileStore
{
    public static final String CHECKSUM_ALGORITHM = "MD5";

    private static final Logger LOGGER = LoggerFactory.getLogger( FileSystemFileStore.class );

    private Path rootLocation;


    public FileSystemFileStore( String rootLocation )
    {
        this.rootLocation = Paths.get( rootLocation );
    }


    @Override
    public boolean contains( byte[] md5 ) throws IOException
    {
        try ( MapDb db = new MapDb( rootLocation ) )
        {
            return db.getMap().containsKey( Hex.encodeHexString( md5 ) );
        }
    }


    @Override
    public InputStream get( byte[] md5 ) throws IOException
    {
        try ( MapDb db = new MapDb( rootLocation ) )
        {
            String path = db.getMap().get( Hex.encodeHexString( md5 ) );
            return path != null ? new FileInputStream( path ) : null;
        }
    }


    @Override
    public boolean get( byte[] md5, File target ) throws IOException
    {
        try ( InputStream is = get( md5 ) )
        {
            if ( is != null )
            {
                Files.copy( is, target.toPath(), StandardCopyOption.REPLACE_EXISTING );
                return true;
            }
        }
        return false;
    }


    @Override
    public byte[] put( File source ) throws IOException
    {
        try ( InputStream is = new FileInputStream( source ) )
        {
            String filename = Files.createTempFile( rootLocation, null, "" ).toFile().getName();
            return put( filename, is );
        }
    }


    @Override
    public byte[] put( URL source ) throws IOException
    {
        try ( InputStream is = source.openStream() )
        {
            String filename = Files.createTempFile( rootLocation, null, "" ).toFile().getName();
            return put( filename, is );
        }
    }


    @Override
    public byte[] put( String filename, InputStream source ) throws IOException
    {
        byte[] checksum;
        try
        {
            checksum = FileHelpers.checksum( source, CHECKSUM_ALGORITHM );
        }
        catch ( NoSuchAlgorithmException ex )
        {
            LOGGER.error( "Failed to create file checksum", ex );
            return null;
        }

        Path target = rootLocation.resolve( firstDigitInHex( checksum ) ).resolve( filename );
        if ( Files.notExists( target.getParent() ) )
        {
            Files.createDirectories( target );
        }
        try ( MapDb db = new MapDb( rootLocation ) )
        {
            // clean up target file in catch clause if copying fails
            Files.copy( source, target, StandardCopyOption.REPLACE_EXISTING );
            db.getMap().put( Hex.encodeHexString( checksum ), target.toAbsolutePath().toString() );
        }
        catch ( IOException ex )
        {
            target.toFile().delete();
            throw ex;
        }
        return checksum;
    }


    @Override
    public boolean remove( byte[] md5 ) throws IOException
    {
        String hex = Hex.encodeHexString( md5 );
        try ( MapDb db = new MapDb( rootLocation ) )
        {
            String p = db.getMap().get( hex );
            if ( p != null )
            {
                Path path = Paths.get( p );
                Files.deleteIfExists( path );
                db.getMap().remove( hex );
                return true;
            }
        }
        return false;
    }


    private String firstDigitInHex( byte[] arr )
    {
        String hex = Hex.encodeHexString( arr );
        return hex.substring( 0, 1 );
    }
}

