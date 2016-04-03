package ai.subut.kurjun.storage.fs;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.model.storage.FileStore;


/**
 * File store backed by a local file system. Mappings of files checksums to their location paths are saved in an
 * embedded database. Actual files are saved under subdirectories of a common parent root directory specified in
 * constructor. Subdirectories are a one-letter directories that correspond to the first letter of the file name.
 */
@SuppressWarnings( "JavadocReference" )
class FileSystemFileStore implements FileStore
{

    private static final String FILEDB_NAME = "checksum.db";
    private static final String MAP_NAME = "checksum-to-filepath";

    private Path rootLocation;


    /**
     * Constructs file system backed file store. File store location in a file system is determined by Kurjun property
     * {@link FileSystemFileStoreModule#ROOT_DIRECTORY} and the supplied context.
     */
    @Inject
    public FileSystemFileStore( KurjunProperties properties, @Assisted KurjunContext context )
    {
        // check if context has explicit location
        String path = properties.getContextProperties( context ).getProperty( KurjunConstants.FILE_STORE_FS_DIR_PATH );
        if ( path != null )
        {
            this.rootLocation = Paths.get( path );
        }
        else
        {
            String parentDirectory = properties.get( KurjunConstants.FILE_STORE_FS_ROOT_DIR );
            this.rootLocation = Paths.get( parentDirectory, context.getName() );
        }
    }


    /**
     * Constructs file system backed file store at the specified file system location.
     *
     * @param parentDirectory parent directory for the file store
     */
    public FileSystemFileStore( String parentDirectory )
    {
        this.rootLocation = Paths.get( parentDirectory );
    }


    @Override
    public boolean contains( String md5 ) throws IOException
    {
        try ( FileDb fileDb = new FileDb( makeDbFilePath() ) )
        {
            return fileDb.contains( MAP_NAME, md5 );
        }
    }


    @Override
    public InputStream get( String md5 ) throws IOException
    {
        try ( FileDb fileDb = new FileDb( makeDbFilePath() ) )
        {
            String path = fileDb.get( MAP_NAME, md5, String.class );

            if ( path != null )
            {
                return new FileInputStream( path );
            }
            else
            {
                return null;
            }
        }
    }


    @Override
    public boolean get( String md5, File target ) throws IOException
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
    public String put( File source ) throws IOException
    {
        try ( InputStream is = new FileInputStream( source ) )
        {
            String filename = UUID.randomUUID().toString().replace( "-", "" );
            return put( filename, is );
        }
    }


    @Override
    public String put( URL source ) throws IOException
    {
        try ( InputStream is = source.openStream() )
        {
            String filename = UUID.randomUUID().toString().replace( "-", "" );
            return put( filename, is );
        }
    }


    @Override
    public String put( String filename, InputStream source ) throws IOException
    {
        Objects.requireNonNull( filename, "Filename" );

        // distribute files into subdirectories by their first letter(s)
        Path subDir = rootLocation.resolve( filename.substring( 0, 2 ) );
        Files.createDirectories( subDir );

        Path target = Files.createTempFile( subDir, filename, "" );
        String md5 = copyStream( source, target );

        try ( FileDb fileDb = new FileDb( makeDbFilePath() ) )
        {
            // check if we already have a file with the calculated md5 checksum, if so just replace the old file
            String existingPath = fileDb.get( MAP_NAME, md5, String.class );
            if ( existingPath != null )
            {
                Files.move( target, Paths.get( existingPath ), StandardCopyOption.REPLACE_EXISTING );
                // clean up
                deleteDirIfEmpty( subDir );
            }
            else
            {
                fileDb.put( MAP_NAME, md5, target.toAbsolutePath().toString() );
            }
        }
        return md5;
    }


    @Override
    public boolean remove( String md5 ) throws IOException
    {
        String hexMd5 = md5;
        try ( FileDb fileDb = new FileDb( makeDbFilePath() ) )
        {
            String path = fileDb.get( MAP_NAME, hexMd5, String.class );
            if ( path != null )
            {
                Path p = Paths.get( path );
                Files.deleteIfExists( p );
                deleteDirIfEmpty( p.getParent() );
                fileDb.remove( MAP_NAME, hexMd5 );
                return true;
            }
        }
        return false;
    }


    @Override
    public long size() throws IOException
    {
        AtomicLong total = new AtomicLong();

        Files.walkFileTree( rootLocation, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) throws IOException
            {
                // sum sizes of only package files, ignore db files
                if ( !file.getFileName().toString().startsWith( FILEDB_NAME ) )
                {
                    total.addAndGet( attrs.size() );
                }
                return FileVisitResult.CONTINUE;
            }


            @Override
            public FileVisitResult visitFileFailed( Path file, IOException exc ) throws IOException
            {
                return FileVisitResult.CONTINUE;
            }
        } );

        return total.get();
    }


    @Override
    public long sizeOf( String md5 ) throws IOException
    {
        try ( FileDb fileDb = new FileDb( makeDbFilePath() ) )
        {
            String path = fileDb.get( MAP_NAME, md5, String.class );
            if ( path != null )
            {
                return Files.size( Paths.get( path ) );
            }
        }
        return 0;
    }


    /**
     * Copies the stream to the file system location specified by path argument.
     *
     * @param source stream to copy
     * @param dest destination path to copy stream to
     *
     * @return MD5 checksum of the stream
     *
     * @throws IOException if i/o errors occur
     */
    private String copyStream( InputStream source, Path dest ) throws IOException
    {
        try ( DigestInputStream is = new DigestInputStream( source, DigestUtils.getMd5Digest() ) )
        {
            Files.copy( is, dest, StandardCopyOption.REPLACE_EXISTING );

            return Hex.encodeHexString( is.getMessageDigest().digest() );
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


    private String makeDbFilePath()
    {
        return rootLocation.resolve( FILEDB_NAME ).toString();
    }
}

