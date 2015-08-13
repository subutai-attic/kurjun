package ai.subut.kurjun.storage.fs;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Hex;

import ai.subut.kurjun.model.storage.FileStore;


/**
 * File store backed by a local file system. Mappings of files checksums to their location paths are saved in an
 * embedded database. Actual files are saved under subdirectories of a common parent root directory specified in
 * constructor. Subdirectories are a one-letter directories that correspond to the first letter of the file name.
 */
class FileSystemFileStore implements FileStore
{
    public static final String MD5 = "MD5";

    private static final Logger LOGGER = LoggerFactory.getLogger( FileSystemFileStore.class );
    private static final int BUFFER_SIZE = 1024 * 8;

    private Path rootLocation;


    /**
     * Initializes this file system backed file store to specified location of a file system.
     *
     * @param rootLocation file system location which will be managed by this file store
     */
    public void init( String rootLocation )
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
            String filename = UUID.randomUUID().toString();
            return put( filename, is );
        }
    }


    @Override
    public byte[] put( URL source ) throws IOException
    {
        try ( InputStream is = source.openStream() )
        {
            String filename = UUID.randomUUID().toString();
            return put( filename, is );
        }
    }


    @Override
    public byte[] put( String filename, InputStream source ) throws IOException
    {
        Objects.requireNonNull( filename, "Filename" );
        Path target = rootLocation.resolve( filename.substring( 0, 1 ) ).resolve( filename );
        Files.createDirectories( target.getParent() );

        Path sourceDump = null;
        try ( MapDb db = new MapDb( rootLocation ) )
        {
            sourceDump = Files.createTempFile( rootLocation, null, null );
            byte[] checksum = dumpStreamAndCalculateChecksum( source, sourceDump );

            // clean up target file in catch clause if operation fails
            Files.move( sourceDump, target, StandardCopyOption.REPLACE_EXISTING );
            db.getMap().put( Hex.encodeHexString( checksum ), target.toAbsolutePath().toString() );
            return checksum;
        }
        catch ( IOException ex )
        {
            target.toFile().delete();
            throw ex;
        }
        finally
        {
            if ( sourceDump != null )
            {
                sourceDump.toFile().delete();
            }
        }
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


    private byte[] dumpStreamAndCalculateChecksum( InputStream source, Path dest ) throws IOException
    {
        try ( DigestInputStream is = new DigestInputStream( source, MessageDigest.getInstance( MD5 ) );
              OutputStream os = new FileOutputStream( dest.toFile() ) )
        {
            int n;
            byte[] buf = new byte[BUFFER_SIZE];
            while ( ( n = is.read( buf ) ) > 0 )
            {
                os.write( buf, 0, n );
            }
            return is.getMessageDigest().digest();
        }
        catch ( NoSuchAlgorithmException ex )
        {
            // should not happen - handle anyways
            throw new IOException( "Failed to open digest input stream", ex );
        }
    }

}

