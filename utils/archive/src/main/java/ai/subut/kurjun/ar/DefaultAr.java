package ai.subut.kurjun.ar;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;


/**
 * Default At implementation.
 */
public class DefaultAr implements Ar {
    public static final int BUFFER_SIZE = 4096;
    private static final Logger LOG = LoggerFactory.getLogger( DefaultAr.class );


    private File file;


    public DefaultAr( File file )
    {
        this.file = file;
    }


    @Override
    public List<ArArchiveEntry> list() throws IOException
    {
        List<ArArchiveEntry> list = new ArrayList<>();
        ArArchiveEntry entry;

        try ( ArArchiveInputStream in = new ArArchiveInputStream( new FileInputStream( file ) ) )
        {
            while ( ( entry = in.getNextArEntry() ) != null ) {
                LOG.debug( "Entry {} added to list.", entry.getName() );
                list.add( entry );
            }
        }
        catch ( IOException e )
        {
            LOG.error( "Cannot access next archive entry.", e );
            throw e;
        }

        return list;
    }


    public void extractFromStream( File outFile, InputStream in, long size ) throws IOException
    {
        extractFromStream( outFile, in, size, BUFFER_SIZE );
    }


    /**
     * A utility function which uses a buffer to write out input stream contents to
     * a file in temporary file storage from the current position of the stream.
     */
    public void extractFromStream( File outFile, InputStream in, long size, int bufferSize ) throws IOException
    {
        byte[] buffer = new byte[ bufferSize ];
        int readAmount;

        try ( FileOutputStream out = new FileOutputStream( outFile ) )
        {
            // user wants it all dumped
            if ( size < 0 )
            {
                while ( ( readAmount = in.read( buffer ) ) != -1 )
                {
                    out.write( buffer, 0, readAmount );
                }
            }
            // user wants only a portion dumped
            else
            {
                while ( ( readAmount = in.read( buffer, 0, Math.min( ( int ) size, bufferSize ) ) ) != -1 && size > 0 )
                {
                    size -= readAmount;
                    out.write( buffer, 0, readAmount );
                }
            }

            out.flush();
        }
        catch ( IOException e )
        {
            LOG.error( "I/O failure while extracting file: {}.", outFile );
            throw e;
        }
    }


    @Override
    public void extract( File extractTo, ArArchiveEntry toExtract ) throws IOException
    {
        ArArchiveEntry entry;

        try ( ArArchiveInputStream in = new ArArchiveInputStream( new FileInputStream( file ) ) )
        {
            while ( ( entry = in.getNextArEntry() ) != null )
            {
                if ( entry.getName().equals( toExtract.getName() ) )
                {
                    LOG.info( "Found matching entry {}, extracting ...", entry.getName() );
                    extractFromStream( extractTo, in, toExtract.getSize() );
                }
            }
        }
        catch ( IOException e )
        {
            LOG.error( "Cannot access next archive.", e );
            throw e;
        }
    }
}
