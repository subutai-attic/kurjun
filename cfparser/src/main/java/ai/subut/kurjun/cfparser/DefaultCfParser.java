package ai.subut.kurjun.cfparser;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vafer.jdeb.debian.BinaryPackageControlFile;

import ai.subut.kurjun.model.PkgMeta;


/**
 * The default CfParser implementation which wraps Torsten Curdt's jdeb
 * files.
 */
public class DefaultCfParser implements CfParser
{
    private final static Logger LOG = LoggerFactory.getLogger( DefaultCfParser.class );


    private boolean isBinaryPackage( File controlFile ) {
        return true;
    }


    private boolean isChangesFile( File controlFile ) {
        return false;
    }


    @Override
    public PkgMeta parse( final File controlFile )
    {
        if ( isBinaryPackage( controlFile ) )
        {
            return parseBinary( controlFile );
        }

        if ( isChangesFile( controlFile ) )
        {
            return parseSource( controlFile );
        }

        throw new IllegalStateException( "We should not goet here." );
    }


    public PkgMeta parseBinary( File controlFile )
    {
        BinaryPackageControlFile control;

        try
        {
            control = new BinaryPackageControlFile( new FileInputStream( controlFile ) );
        }
        catch ( IOException | ParseException e )
        {
            LOG.error( "Failed to parse control file: {}", controlFile.getAbsolutePath(), e );
        }



        return null;
    }


    public PkgMeta parseSource( File controlFile ) {
        return null;
    }
}
