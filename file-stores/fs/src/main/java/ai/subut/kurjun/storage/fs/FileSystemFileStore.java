package ai.subut.kurjun.storage.fs;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.model.storage.FileStore;

import static ai.subut.kurjun.storage.fs.FileSystemFileStoreModule.ROOT_DIRECTORY;


/**
 * File store backed by a local file system. Mappings of files checksums to their location paths are saved in an
 * embedded database. Actual files are saved under subdirectories of a common parent root directory specified in
 * constructor. Subdirectories are a one-letter directories that correspond to the first letter of the file name.
 */
class FileSystemFileStore implements FileStore
{

    private static final Logger LOGGER = LoggerFactory.getLogger( FileSystemFileStore.class );
    private static final int BUFFER_SIZE = 1024 * 8;
    private static final String MAP_NAME = "checksum-to-filepath";

    private Path rootLocation;
    private FileDb fileDb;


    /**
     * Constructs file system backed file store to a specified location in a file system.
     *
     */
    @Inject
    public FileSystemFileStore( @Named( ROOT_DIRECTORY ) String rootLocation )
    {
        this.rootLocation = Paths.get( rootLocation );
        try
        {
            this.fileDb = new FileDb( this.rootLocation.resolve( "checksum.db" ).toString() );
        }
        catch ( IOException ex )
        {
            LOGGER.error( "Failed to initialize db file", ex );
            throw new IllegalArgumentException( "Failed to initialize db file" );
        }
    }


    @Override
    public boolean contains( byte[] md5 ) throws IOException
    {
        return fileDb.contains( MAP_NAME, Hex.encodeHexString( md5 ) );
    }


    @Override
    public InputStream get( byte[] md5 ) throws IOException
    {
        String path = fileDb.get( MAP_NAME, Hex.encodeHexString( md5 ), String.class );
        return path != null ? new FileInputStream( path ) : null;
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
            String filename = UUID.randomUUID().toString().replace( "-", "" );
            return put( filename, is );
        }
    }


    @Override
    public byte[] put( URL source ) throws IOException
    {
        try ( InputStream is = source.openStream() )
        {
            String filename = UUID.randomUUID().toString().replace( "-", "" );
            return put( filename, is );
        }
    }


    @Override
    public byte[] put( String filename, InputStream source ) throws IOException
    {
        Objects.requireNonNull( filename, "Filename" );

        // distribute files into subdirectories by their first letter(s)
        Path subDir = rootLocation.resolve( filename.substring( 0, 2 ) );
        Files.createDirectory( subDir );

        Path target = Files.createTempFile( subDir, filename, null );
        byte[] md5 = copyStream( source, target );

        // check if we already have a file with the calculated md5 checksum, if so just replace the old file
        String existingPath = fileDb.get( MAP_NAME, Hex.encodeHexString( md5 ), String.class );
        if ( existingPath != null )
        {
            Files.move( target, Paths.get( existingPath ), StandardCopyOption.REPLACE_EXISTING );
            // clean up
            deleteDirIfEmpty( subDir );
        }
        else
        {
            fileDb.put( MAP_NAME, Hex.encodeHexString( md5 ), target.toAbsolutePath().toString() );
        }
        return md5;
    }


    @Override
    public boolean remove( byte[] md5 ) throws IOException
    {
        String hexMd5 = Hex.encodeHexString( md5 );
        String path = fileDb.get( MAP_NAME, hexMd5, String.class );
        if ( path != null )
        {
            Files.deleteIfExists( Paths.get( path ) );
            fileDb.remove( MAP_NAME, hexMd5 );
            return true;
        }
        return false;
    }


    /**
     * Copies the stream to the file system location specified by path argument.
     *
     * @param source stream to copy
     * @param dest destination path to copy stream to
     * @return MD5 checksum of the stream
     * @throws IOException if i/o errors occur
     */
    private byte[] copyStream( InputStream source, Path dest ) throws IOException
    {
        try ( DigestInputStream is = new DigestInputStream( source, DigestUtils.getMd5Digest() );
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
    }


    private void deleteDirIfEmpty( Path dir ) throws IOException
    {
        try ( DirectoryStream ds = Files.newDirectoryStream( dir ) )
        {
            if ( ds.iterator().hasNext() )
            {
                // this directory is not empty, go back
                return;
            }
        }
        Files.delete( dir );
    }


}

