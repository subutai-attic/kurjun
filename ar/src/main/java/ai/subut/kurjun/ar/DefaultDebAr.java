package ai.subut.kurjun.ar;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;

import com.google.common.base.Preconditions;

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

    private final DefaultAr ar;
    private final File tmpDir;


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
        this.tmpDir = tmpDir;

        // check proper archive: (1) check that we have required entry count
        checkNotNull( debFile, "The debian archive file cannot be null." );
        ar = new DefaultAr( debFile );
        List<ArArchiveEntry> entries = ar.list();
        checkPositionIndexes( 0, 2, entries.size() );

        // check proper archive: (2) check that first entry (debian-binary) has 2.0
        ArArchiveEntry debBinEntry = entries.get( 0 );
        checkState( debBinEntry.getName().equals( DEBIAN_BINARY ),
                "The first Debian archive entry must be " + DEBIAN_BINARY );
        File debBinFile = new File( tmpDir, DEBIAN_BINARY );
        ar.extract( debBinFile, debBinEntry );
        checkState( DEBIAN_VERSION.equals( FileUtils.readFileToString( debBinFile ) ) );

        // check proper archive: (3) check that second entry is control tarball
        ArArchiveEntry debCtrlEntry = entries.get( 1 );
        checkState( debCtrlEntry.getName().startsWith( DEBIAN_CONTROL ),
                "The second Debian archive entry must be " + DEBIAN_CONTROL );

        // extract the control tarball from deb archive
        File debCtrlTarball = new File( tmpDir, debCtrlEntry.getName() );
        ar.extract( debCtrlTarball, debCtrlEntry );

        // decompress and extract the control tarball
        DefaultTar tar = new DefaultTar( debCtrlTarball );

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
        return null;
    }


    @Override
    public File getMd5Sums()
    {
        return null;
    }


    @Override
    public boolean isVersion2()
    {
        return false;
    }
}
