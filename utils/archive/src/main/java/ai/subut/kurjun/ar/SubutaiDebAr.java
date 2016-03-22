package ai.subut.kurjun.ar;


import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.compress.archivers.ar.ArArchiveEntry;


/**
 * Debian Archive with Subutai specific configuration file.
 *
 */
public class SubutaiDebAr extends DefaultDebAr
{
    public static final String SUBUTAI_CONFIG = "config";

    private File configFile;


    /**
     * Creates {@link DebAr} implementations that accesses the contents of a Subutai specific Debian archive using
     * supplied directory.
     *
     * @param debFile Debian archive file
     * @param tmpDir directory where archive contents are extracted
     * @throws IOException
     */
    public SubutaiDebAr( File debFile, File tmpDir ) throws IOException
    {
        super( debFile, tmpDir );

        // get list of entries
        Ar ar = new DefaultAr( debFile );
        List<ArArchiveEntry> entries = ar.list();

        // third entry shall be data archive 
        ArArchiveEntry debDataEntry = entries.get( 2 );
        if ( !debDataEntry.getName().startsWith( DEBIAN_DATA ) )
        {
            throw new IllegalStateException( "Data archive not found in Debian package." );
        }

        // extract data archive file
        File debDataTarball = new File( tmpDir, debDataEntry.getName() );
        ar.extract( debDataTarball, debDataEntry );

        // explode data archive file
        Tar tar = new DefaultTar( debDataTarball );
        tar.extract( debDataTarball.getParentFile() );

        File subutaiConfigFile = new File( debDataTarball.getParentFile(), SUBUTAI_CONFIG );
        if ( subutaiConfigFile.exists() )
        {
            this.configFile = subutaiConfigFile;
        }

    }


    /**
     * Gets a file that points to Subutai config file extracted to the temporary directory supplied in constructor.
     *
     * @return
     */
    public File getConfigFile()
    {
        return configFile;
    }

}

