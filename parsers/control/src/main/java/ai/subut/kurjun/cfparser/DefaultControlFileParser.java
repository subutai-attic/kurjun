package ai.subut.kurjun.cfparser;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vafer.jdeb.debian.BinaryPackageControlFile;

import ai.subut.kurjun.cfparser.service.ControlFileParser;
import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.metadata.PackageMetadata;


/**
 * The default CfParser implementation which wraps Torsten Curdt's jdeb files.
 */
public class DefaultControlFileParser implements ControlFileParser
{
    private final static Logger LOG = LoggerFactory.getLogger( DefaultControlFileParser.class );


//    private boolean isBinaryPackage( File controlFile ) {
//        return true;
//    }
//
//
//    private boolean isChangesFile( File controlFile ) {
//        return false;
//    }
    @Override
    public PackageMetadata parse( Map<String, Object> params, File controlFile )
    {
//        if ( isBinaryPackage( controlFile ) )
//        {
//            return parseBinary( controlFile );
//        }
//
//        if ( isChangesFile( controlFile ) )
//        {
//            return parseSource( controlFile );
//        }
//
//        throw new IllegalStateException( "We should not goet here." );
        return parseBinary( params, controlFile );
    }


    public PackageMetadata parseBinary( Map<String, Object> params, File controlFile )
    {
        BinaryPackageControlFile control = null;

        try
        {
            control = new BinaryPackageControlFile( new FileInputStream( controlFile ) );
        }
        catch ( IOException | ParseException e )
        {
            LOG.error( "Failed to parse control file: {}", controlFile.getAbsolutePath(), e );
        }

        BinaryPackageMetadata metadata = new BinaryPackageMetadata( ( byte[] ) params.get( "md5sum" ),
                                                                    ( String ) params.get( "filename" ), control );

        return MetadataUtils.serializablePackageMetadata( metadata );
    }
//
//
//    public PkgMeta parseSource( File controlFile ) {
//        return null;
//    }
}
