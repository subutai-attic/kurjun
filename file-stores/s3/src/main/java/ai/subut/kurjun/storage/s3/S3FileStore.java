package ai.subut.kurjun.storage.s3;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
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
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.model.storage.FileStore;


class S3FileStore implements FileStore
{

    public static final long MULTIPART_THRESHOLD_BYTES = 1024 * 1024 * 100;

    private static final Logger LOGGER = LoggerFactory.getLogger( S3FileStore.class );

    String bucketName;
    AmazonS3 s3client;


    @Inject
    public S3FileStore( AWSCredentials credentials, KurjunProperties properties, @Assisted KurjunContext context )
    {
        // if there is explicit bucket name for context then use that
        Properties prop = properties.getContextProperties( context.getName() );
        String bucket = prop.getProperty( S3FileStoreModule.BUCKET_NAME );

        // if bucket name is not supplied, make default bucket name for context
        if ( bucket == null )
        {
            bucket = makeBucketName( properties, context );
        }

        ctor( credentials, bucket );
    }


    public S3FileStore( AWSCredentials credentials, String bucketName )
    {
        ctor( credentials, bucketName );
    }


    private void ctor( AWSCredentials credentials, String bucketName )
    {
        this.bucketName = bucketName;
        this.s3client = new AmazonS3Client( credentials );

        if ( !s3client.doesBucketExist( bucketName ) )
        {
            Bucket bucket = s3client.createBucket( bucketName );
            LOGGER.info( "Bucket '{}' created", bucket.getName() );
        }
    }


    public static final String makeBucketName( KurjunProperties properties, KurjunContext context )
    {
        String prefix = properties.getWithDefault( S3FileStoreModule.BUCKET_NAME_PREFIX, "kurjun-bucket-" );
        // clear out all non-word characters; Amazon S3 bucket names have some restritions on naming
        String contextCleared = context.getName().replaceAll( "\\W", "" );
        return prefix + contextCleared;
    }


    @Override
    public boolean contains( String md5 ) throws IOException
    {
        String hex = md5;
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
    public InputStream get( String md5 ) throws IOException
    {
        Objects.requireNonNull( md5, "Checksum" );
        String hex = md5;

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
    public boolean get( String md5, File target ) throws IOException
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
    public String put( File source ) throws IOException
    {
        Objects.requireNonNull( source, "Source file" );

        byte[] md5;

        try ( InputStream is = new FileInputStream( source ) )
        {
            md5 = DigestUtils.md5( is );
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
        return hex;
    }


    @Override
    public String put( URL source ) throws IOException
    {
        Objects.requireNonNull( source, "Source URL" );
        File file = File.createTempFile( "s3_", null );
        try ( InputStream is = source.openStream() )
        {
            Files.copy( is, file.toPath(), StandardCopyOption.REPLACE_EXISTING );
            return put( file );
        }
        finally
        {
            file.delete();
        }
    }


    @Override
    public String put( String filename, InputStream source ) throws IOException
    {
        // filename IS IGNORED in this implementation!!!

        Objects.requireNonNull( source, "Source stream" );
        File file = File.createTempFile( "s3_", null );
        try
        {
            Files.copy( source, file.toPath(), StandardCopyOption.REPLACE_EXISTING );
            return put( file );
        }
        finally
        {
            file.delete();
        }
    }


    @Override
    public boolean remove( String md5 ) throws IOException
    {
        Objects.requireNonNull( md5, "Checksum" );
        String hex = md5;
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


    @Override
    public long size() throws IOException
    {
        // Amazon S3 api does not provide methods to directly get size of the bucket.
        // Here we retrieve object summaries of the bucket and add up size of each object.

        long total = 0;
        ObjectListing listing = s3client.listObjects( bucketName );

        for ( S3ObjectSummary obj : listing.getObjectSummaries() )
        {
            total += obj.getSize();
        }

        while ( listing.isTruncated() )
        {
            listing = s3client.listNextBatchOfObjects( listing );
            for ( S3ObjectSummary obj : listing.getObjectSummaries() )
            {
                total += obj.getSize();
            }
        }

        return total;
    }


    @Override
    public long sizeOf( String md5 ) throws IOException
    {
        ListObjectsRequest lor = new ListObjectsRequest();
        lor.setBucketName( bucketName );
        lor.setPrefix( makeKey( md5 ) );

        // here we list items by supplied md5 digest value which is a uniquely identifying parameter
        // so there is no checks if listing is truncated or not. The listing can have one value or any.
        ObjectListing listing = s3client.listObjects( lor );
        List<S3ObjectSummary> items = listing.getObjectSummaries();
        return items.isEmpty() ? 0 : items.get( 0 ).getSize();
    }


    private String makeKey( String s )
    {
        return s.substring( 0, 2 ) + "/" + s;
    }

    @Override public byte[] put(InputStream source) throws IOException {
        return new byte[0];
    }
}

