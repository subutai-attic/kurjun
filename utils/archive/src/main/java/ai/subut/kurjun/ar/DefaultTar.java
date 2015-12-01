package ai.subut.kurjun.ar;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import static com.google.common.base.Preconditions.checkState;


/**
 * The default Tar interface implementation.
 */
public class DefaultTar implements Tar
{

    private static final Logger LOG = LoggerFactory.getLogger( DefaultTar.class );
    private final File file;
    private File decompressed;


    /**
     * The Tar file to use.
     *
     * @param file the Tar file
     */
    public DefaultTar( File file )
    {
        this.file = file;
    }


    @Override
    public File getFile()
    {
        return file;
    }


    private void decompress() throws IOException
    {
        decompressed = new File( file.getParent(), file.getName().substring( 0, file.getName().lastIndexOf( '.' ) ) );
        CompressionType compressionType = CompressionType.getCompressionType( file );
        InputStream in;
        OutputStream out = new FileOutputStream( decompressed );

        switch ( compressionType )
        {
            case XZ:
                in = new XZCompressorInputStream( new FileInputStream( file ) );
                break;
            case GZIP:
                in = new GZIPInputStream( new FileInputStream( file ) );
                break;
            case BZIP2:
                in = new BZip2CompressorInputStream( new FileInputStream( file ) );
                break;
            case LZMA:
                in = new LZMACompressorInputStream( new FileInputStream( file ) );
                break;
            case NONE:
                in = null;
                break;
            default:
                in = null;
                break;
        }

        if ( in != null )
        {
            IOUtils.copy( in, out );
            in.close();
        }

        out.flush();
        out.close();
    }


    @Override
    public void extract( final File extractTo ) throws IOException
    {
        if ( !extractTo.exists() )
        {
            checkState( extractTo.mkdirs() );
        }

        File tarfile;

        if ( CompressionType.isCompressed( file ) )
        {
            decompress();
            tarfile = decompressed;
        }
        else
        {
            tarfile = file;
        }

        try ( TarArchiveInputStream in = new TarArchiveInputStream( new FileInputStream( tarfile ) ) )
        {
            TarArchiveEntry entry;
            while ( ( entry = in.getNextTarEntry() ) != null )
            {
                if ( entry.isDirectory() )
                {
                    File dir = new File( extractTo, entry.getName() );

                    if ( !dir.exists() )
                    {
                        checkState( dir.mkdirs() );
                    }
                }
                else if ( entry.isFile() )
                {
                    File dir = new File( extractTo, entry.getName() );

                    if ( !dir.getParentFile().exists() )
                    {
                        checkState( dir.getParentFile().mkdirs() );
                    }
                    
                    int readBytes;
                    byte[] buffer = new byte[1024];
                    File outFile = new File( extractTo, entry.getName() );

                    try ( FileOutputStream out = new FileOutputStream( outFile ) )
                    {
                        while ( ( readBytes = in.read( buffer ) ) != -1 )
                        {
                            out.write( buffer, 0, readBytes );
                        }
                        out.flush();
                    }
                    catch ( IOException eout )
                    {
                        LOG.error( "Failed to write to file {}", outFile.getCanonicalFile(), eout );
                        throw eout;
                    }
                }
            }
        }
        catch ( IOException e )
        {
            LOG.error( "Failed to read from tar file {}", tarfile.getCanonicalFile(), e );
            throw e;
        }
        finally
        {
            if ( decompressed != null )
            {
                FileUtils.deleteQuietly( decompressed );
            }
        }
    }
}
