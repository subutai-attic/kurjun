package ai.subut.kurjun.ar;


import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.io.FileUtils;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndexes;
import static com.google.common.base.Preconditions.checkState;


/**
 * Default implementation of DebAr.
 */
public class DefaultDebAr implements DebAr
{
    public static final String DEBIAN_BINARY  = "debian-binary";
    public static final String DEBIAN_VERSION = "2.0";
    public static final String DEBIAN_CONTROL = "control.tar";
    public static final String DEBIAN_DATA = "data.tar";

    private final File controlFile;
    private final File md5sumsFile;

    private static final Logger LOG = LoggerFactory.getLogger( DefaultDebAr.class );


    /**
     * Accesses the contents of a Debian archive using a temporary directory.
     *
     * @param debFile the Debian archive file
     * @param tmpDir the temporary directory
     */
    public DefaultDebAr ( File debFile, File tmpDir ) throws IOException
    {
        // check proper temp directory
        checkNotNull( tmpDir, "The extraction tmpDir must not be null." );

        if ( ! tmpDir.exists() )
        {
            checkState( tmpDir.mkdirs() );
        }

        // check proper archive: (1) check that we have required entry count
        checkNotNull( debFile, "The debian archive file cannot be null." );
        final DefaultAr ar = new DefaultAr( debFile );
        List<ArArchiveEntry> entries = ar.list();
        checkPositionIndexes( 0, 2, entries.size() );

        // check proper archive: (2) check that first entry (debian-binary) has 2.0
        ArArchiveEntry debBinEntry = entries.get( 0 );
        checkState( debBinEntry.getName().equals( DEBIAN_BINARY ),
                "The first Debian archive entry must be " + DEBIAN_BINARY );
        File debBinFile = new File( tmpDir, DEBIAN_BINARY );
        ar.extract( debBinFile, debBinEntry );
        String contents = FileUtils.readFileToString( debBinFile ).trim();
        checkState( DEBIAN_VERSION.equals( contents ), "Invalid Debian version " + DEBIAN_VERSION );

        // check proper archive: (3) check that second entry is control tarball
        ArArchiveEntry debCtrlEntry = entries.get( 1 );
        checkState( debCtrlEntry.getName().startsWith( DEBIAN_CONTROL ),
                "The second Debian archive entry must be " + DEBIAN_CONTROL );

        // extract the control tarball from deb archive
        File debCtrlTarball = new File( tmpDir, debCtrlEntry.getName() );
        ar.extract( debCtrlTarball, debCtrlEntry );

        // decompress and extract the control tarball
        DefaultTar tar = new DefaultTar( debCtrlTarball );
        tar.extract( debCtrlTarball.getParentFile() );
        controlFile = new File( debCtrlTarball.getParentFile(), "control" );
        checkState( controlFile.exists(), "'control' does not exist" );
        md5sumsFile = new File( debCtrlTarball.getParentFile(), "md5sums" );
        checkState( md5sumsFile.exists(), "'md5sums' does not exist" );
    }


    /**
     * Extracts the archive into a folder that is a peer of the debian file, using
     * the same base name as the file without the extension.
     *
     * @param debFile the Debian archive file
     * @throws IOException if there are problems accessing the Debian archive
     */
    public DefaultDebAr ( File debFile ) throws IOException
    {
        this( debFile, new File( debFile.getParentFile(),
                debFile.getName().substring( 0, debFile.getName().lastIndexOf( '.' ) ) ) );
    }


    @Override
    public File getControlFile()
    {
        return controlFile;
    }


    @Override
    public File getMd5Sums()
    {
        return md5sumsFile;
    }
}
