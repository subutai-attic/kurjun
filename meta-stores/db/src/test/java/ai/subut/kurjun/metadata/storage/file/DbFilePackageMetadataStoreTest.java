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


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ai.subut.kurjun.metadata.common.PackageMetadataImpl;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.PackageMetadata;
import ai.subut.kurjun.model.metadata.Priority;


public class DbFilePackageMetadataStoreTest
{

    private static DbFilePackageMetadataStore store;

    private PackageMetadata meta;
    private byte[] otherMd5;


    @BeforeClass
    public static void setUpClass() throws IOException
    {
        store = new DbFilePackageMetadataStore( Files.createTempDirectory( null ).toString() );
    }


    @AfterClass
    public static void tearDownClass() throws IOException
    {
        // clean up specified location
        Files.walkFileTree( store.location, new FileDeleter() );
        Files.delete( store.location );
    }


    @Before
    public void setUp() throws IOException, NoSuchAlgorithmException
    {
        PackageMetadataImpl pm = new PackageMetadataImpl();
        pm.setArchitecture( Architecture.amd64 );
        pm.setDescription( "Description here" );
        pm.setFilename( "package-name-ver-arch.deb" );
        pm.setInstalledSize( 1234 );
        pm.setMaintainer( "Maintainer" );
        pm.setMd5( checksum( new ByteArrayInputStream( "contents".getBytes() ) ) );
        pm.setPriority( Priority.important );

        meta = pm;
        store.put( meta );

        otherMd5 = checksum( new ByteArrayInputStream( "other content".getBytes() ) );
    }


    @After
    public void tearDown()
    {
    }


    @Test
    public void testContains() throws Exception
    {
        Assert.assertTrue( store.contains( meta.getMd5Sum() ) );
        Assert.assertFalse( store.contains( otherMd5 ) );
    }


    @Test
    public void testGet() throws Exception
    {
        PackageMetadata res = store.get( meta.getMd5Sum() );
        Assert.assertEquals( meta, res );
        Assert.assertNull( store.get( otherMd5 ) );
    }


    @Test
    public void testPut() throws Exception
    {
        // already exists
        Assert.assertFalse( store.put( meta ) );
    }


    @Test
    public void testRemove() throws Exception
    {
        // does not exist
        Assert.assertFalse( store.remove( otherMd5 ) );

        // removed first then does not exist anymore
        Assert.assertTrue( store.remove( meta.getMd5Sum() ) );
        Assert.assertFalse( store.remove( meta.getMd5Sum() ) );
    }


    public static byte[] checksum( InputStream is ) throws NoSuchAlgorithmException, IOException
    {
        MessageDigest md = MessageDigest.getInstance( "MD5" );
        int len;
        byte[] buf = new byte[1024];
        while ( ( len = is.read( buf ) ) != -1 )
        {
            md.update( buf, 0, len );
        }
        return md.digest();
    }


    private static class FileDeleter extends SimpleFileVisitor<Path>
    {
        @Override
        public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) throws IOException
        {
            if ( file.toFile().isFile() )
            {
                Files.delete( file );
            }
            return FileVisitResult.CONTINUE;
        }
    }


}

