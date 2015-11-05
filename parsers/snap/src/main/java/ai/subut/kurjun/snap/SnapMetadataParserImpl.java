package ai.subut.kurjun.snap;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.yaml.snakeyaml.Yaml;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import com.google.inject.Inject;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.ar.DefaultTar;
import ai.subut.kurjun.ar.Tar;
import ai.subut.kurjun.common.utils.SnapUtils;
import ai.subut.kurjun.metadata.common.snap.DefaultSnapMetadata;
import ai.subut.kurjun.model.metadata.snap.SnapMetadata;
import ai.subut.kurjun.snap.service.SnapMetadataParser;


class SnapMetadataParserImpl implements SnapMetadataParser
{

    Yaml yaml;


    @Inject
    public SnapMetadataParserImpl( Yaml yaml )
    {
        this.yaml = yaml;
    }


    @Override
    public SnapMetadata parse( File packageFile ) throws IOException
    {
        byte[] md5 = calculateMd5Checksum( packageFile );

        Path target = Files.createTempDirectory( null );
        try
        {
            Tar tar = new DefaultTar( packageFile );
            tar.extract( target.toFile() );

            Path pathToPackageMetadata = target.resolve( "meta/package.yaml" );
            try ( InputStream is = new FileInputStream( pathToPackageMetadata.toFile() ) )
            {
                return parseMetadataFile( is, md5 );
            }
        }
        finally
        {
            FileUtils.deleteDirectory( target.toFile() );
        }
    }


    @Override
    public SnapMetadata parse( InputStream packageStream ) throws IOException
    {
        return parse( packageStream, CompressionType.NONE );
    }


    @Override
    public SnapMetadata parse( InputStream packageStream, CompressionType compressionType ) throws IOException
    {
        String ext = null;
        if ( compressionType != null && compressionType != CompressionType.NONE )
        {
            ext = "." + compressionType.getExtension();
        }

        Path target = Files.createTempFile( null, ext );
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
        return parseMetadataFile( metadataFileStream, null );
    }


    private DefaultSnapMetadata parseMetadataFile( InputStream stream, byte[] md5 )
    {
        DefaultSnapMetadata m = yaml.loadAs( stream, DefaultSnapMetadata.class );

        if ( !SnapUtils.isValidName( m.getName() ) )
        {
            throw new IllegalArgumentException( "Invalid package name: " + m.getName() );
        }
        if ( !SnapUtils.isValidVersion( m.getVersion() ) )
        {
            throw new IllegalArgumentException( "Invalid package version: " + m.getVersion() );
        }

        if ( md5 != null )
        {
            m.setMd5Sum( md5 );
        }

        return m;
    }


    private byte[] calculateMd5Checksum( File file ) throws IOException
    {
        try ( InputStream is = new FileInputStream( file ) )
        {
            return DigestUtils.md5( is );
        }
    }

}

