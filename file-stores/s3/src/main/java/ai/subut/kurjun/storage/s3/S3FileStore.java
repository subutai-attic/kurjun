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
package ai.subut.kurjun.storage.s3;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Hex;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

import ai.subut.kurjun.model.storage.FileStore;


public class S3FileStore implements FileStore
{

    public static final String BUCKET_NAME = "subutai-kurjun";
    public static final long MULTIPART_THRESHOLD_BYTES = 1024 * 1024 * 100;

    private static final Logger LOGGER = LoggerFactory.getLogger( S3FileStore.class );
    private static final int BUFFER_SIZE = 1024 * 8;

    String bucketName;
    AmazonS3 s3client;


    public S3FileStore( String bucketName )
    {
        this.bucketName = bucketName;
        this.s3client = new AmazonS3Client( new KurjunAWSCredentialsProvider() );

        if ( !s3client.doesBucketExist( bucketName ) )
        {
            Bucket bucket = s3client.createBucket( bucketName );
            LOGGER.info( "Bucket '{}' created", bucket.getName() );
        }
    }


    @Override
    public boolean contains( byte[] md5 ) throws IOException
    {
        String hex = Hex.encodeHexString( md5 );
        String key = makeKey( hex );

        ListObjectsRequest lor = new ListObjectsRequest();
        lor.setBucketName( bucketName );
        lor.setPrefix( key );

        ObjectListing listing = s3client.listObjects( lor );
        for ( S3ObjectSummary obj : listing.getObjectSummaries() )
        {
            if ( obj.getKey().equalsIgnoreCase( key ) )
            {
                return true;
            }
        }
        while ( listing.isTruncated() )
        {
            listing = s3client.listNextBatchOfObjects( listing );
            for ( S3ObjectSummary obj : listing.getObjectSummaries() )
            {
                if ( obj.getKey().equalsIgnoreCase( key ) )
                {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public InputStream get( byte[] md5 ) throws IOException
    {
        Objects.requireNonNull( md5, "Checksum" );
        String hex = Hex.encodeHexString( md5 );

        GetObjectRequest gor = new GetObjectRequest( bucketName, makeKey( hex ) );
        try
        {
            S3Object obj = s3client.getObject( gor );
            return obj != null ? obj.getObjectContent() : null;
        }
        catch ( AmazonClientException ex )
        {
            LOGGER.warn( "Get by checksum failed: {}", ex.getMessage() );
            return null;
        }
    }


    @Override
    public boolean get( byte[] md5, File target ) throws IOException
    {
        Objects.requireNonNull( target, "Target file" );
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
        Objects.requireNonNull( source, "Source file" );
        byte[] md5;
        try ( InputStream is = new FileInputStream( source ) )
        {
            md5 = checksum( is );
        }
        String hex = Hex.encodeHexString( md5 );

        PutObjectRequest por = new PutObjectRequest( bucketName, makeKey( hex ), source );
        if ( source.length() < MULTIPART_THRESHOLD_BYTES )
        {
            s3client.putObject( por );
        }
        else
        {
            TransferManager tm = new TransferManager( s3client );
            Upload upload = tm.upload( por );
            try
            {
                upload.waitForCompletion();
            }
            catch ( AmazonClientException | InterruptedException ex )
            {
                throw new IOException( "Failed to upload large file", ex );
            }
            finally
            {
                tm.shutdownNow( false );
            }
        }
        return md5;
    }


    @Override
    public byte[] put( URL source ) throws IOException
    {
        Objects.requireNonNull( source, "Source URL" );
        File file = File.createTempFile( "s3_", null );
        try ( OutputStream os = new FileOutputStream( file ); InputStream is = source.openStream() )
        {
            int n;
            byte[] buf = new byte[BUFFER_SIZE];
            while ( ( n = is.read( buf ) ) > 0 )
            {
                os.write( buf, 0, n );
            }
            return put( file );
        }
        finally
        {
            file.delete();
        }
    }


    @Override
    public byte[] put( String filename, InputStream source ) throws IOException
    {
        // filename IS IGNORED in this implementation!!!

        Objects.requireNonNull( source, "Source stream" );
        File file = File.createTempFile( "s3_", null );
        try ( OutputStream os = new FileOutputStream( file ) )
        {
            int n;
            byte[] buf = new byte[BUFFER_SIZE];
            while ( ( n = source.read( buf ) ) > 0 )
            {
                os.write( buf, 0, n );
            }
            return put( file );
        }
        finally
        {
            file.delete();
        }
    }


    @Override
    public boolean remove( byte[] md5 ) throws IOException
    {
        Objects.requireNonNull( md5, "Checksum" );
        String hex = Hex.encodeHexString( md5 );
        if ( contains( md5 ) )
        {
            DeleteObjectRequest dor = new DeleteObjectRequest( bucketName, makeKey( hex ) );
            s3client.deleteObject( dor );
            return true;
        }
        else
        {
            return false;
        }
    }


    private String makeKey( String s )
    {
        return s.substring( 0, 2 ) + "/" + s;
    }


    private static byte[] checksum( InputStream is ) throws IOException
    {
        int len;
        byte[] buf = new byte[BUFFER_SIZE];
        try
        {
            MessageDigest md = MessageDigest.getInstance( "MD5" );
            while ( ( len = is.read( buf ) ) != -1 )
            {
                md.update( buf, 0, len );
            }
            return md.digest();
        }
        catch ( NoSuchAlgorithmException ex )
        {
            throw new IOException( "Failed to calculate checksum of file", ex );
        }
    }

}

