package ai.subut.kurjun.snap;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.yaml.snakeyaml.Yaml;

import org.apache.commons.io.FileUtils;

import com.google.inject.Inject;

import ai.subut.kurjun.ar.DefaultTar;
import ai.subut.kurjun.ar.Tar;
import ai.subut.kurjun.model.metadata.snap.SnapMetadata;
import ai.subut.kurjun.model.metadata.snap.SnapUtils;
import ai.subut.kurjun.snap.service.SnapMetadataParser;


class SnappyMetadataParserImpl implements SnapMetadataParser
{

    @Inject
    Yaml yaml;


    @Override
    public SnapMetadata parse( File packageFile ) throws IOException
    {
        Path target = Files.createTempDirectory( null );
        try
        {
            Tar tar = new DefaultTar( packageFile );
            tar.extract( target.toFile() );
            Path pathToPackageMetadata = target.resolve( "meta/package.yaml" );
            return parseMetadata( pathToPackageMetadata.toFile() );
        }
        finally
        {
            FileUtils.deleteDirectory( target.toFile() );
        }
    }


    @Override
    public SnapMetadata parse( InputStream packageStream ) throws IOException
    {
        Path target = Files.createTempFile( null, null );
        try
        {
            Files.copy( packageStream, target, StandardCopyOption.REPLACE_EXISTING );
            return parse( target.toFile() );
        }
        finally
        {
            Files.delete( target );
        }
    }


    @Override
    public SnapMetadata parseMetadata( File metadataFile ) throws IOException
    {
        try ( InputStream is = new FileInputStream( metadataFile ) )
        {
            return parseMetadata( is );
        }
    }


    @Override
    public SnapMetadata parseMetadata( InputStream metadataFileStream ) throws IOException
    {
        DefaultSnapMetadata m = yaml.loadAs( metadataFileStream, DefaultSnapMetadata.class );

        if ( !SnapUtils.isValidName( m.getName() ) )
        {
            throw new IllegalArgumentException( "Invalid package name: " + m.getName() );
        }
        if ( !SnapUtils.isValidVersion( m.getVersion() ) )
        {
            throw new IllegalArgumentException( "Invalid package version: " + m.getVersion() );
        }

        return m;
    }


}

