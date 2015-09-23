package ai.subut.kurjun.subutai;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import ai.subut.kurjun.ar.DefaultTar;
import ai.subut.kurjun.ar.Tar;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.subutai.service.SubutaiTemplateParser;


class SubutaiTemplateParserImpl implements SubutaiTemplateParser
{

    public static final Pattern NAME_LINE_PATTERN = Pattern.compile( "lxc.utsname\\s*=\\s*(.+)" );
    public static final Pattern ARCH_LINE_PATTERN = Pattern.compile( "lxc.arch\\s*=\\s*(.+)" );
    public static final Pattern VERSION_LINE_PATTERN = Pattern.compile( "subutai.template.version\\s*=\\s*(.+)" );


    @Override
    public SubutaiTemplateMetadata parseTemplate( File file ) throws IOException
    {
        byte[] md5;
        try ( InputStream is = new FileInputStream( file ) )
        {
            md5 = DigestUtils.md5( is );
        }

        Path targetDir = Files.createTempDirectory( null );
        try
        {
            Tar tar = new DefaultTar( file );
            tar.extract( targetDir.toFile() );

            Path configPath = targetDir.resolve( "config" );
            try ( InputStream is = new FileInputStream( configPath.toFile() ) )
            {
                return parseConfigFile( is, md5 );
            }
        }
        finally
        {
            FileUtils.deleteDirectory( targetDir.toFile() );
        }
    }


    @Override
    public SubutaiTemplateMetadata parseTemplateConfigFile( InputStream stream ) throws IOException
    {
        return parseConfigFile( stream, null );
    }


    private SubutaiTemplateMetadata parseConfigFile( InputStream stream, byte[] md5 ) throws IOException
    {
        String name = null;
        String version = null;
        Architecture arch = null;

        try ( BufferedReader br = new BufferedReader( new InputStreamReader( stream ) ) )
        {
            String line;
            while ( ( line = br.readLine() ) != null )
            {
                Matcher matcher = NAME_LINE_PATTERN.matcher( line );
                if ( matcher.matches() )
                {
                    name = matcher.group( 1 );
                }
                else if ( ( matcher = VERSION_LINE_PATTERN.matcher( line ) ).matches() )
                {
                    version = matcher.group( 1 );
                }
                else if ( ( matcher = ARCH_LINE_PATTERN.matcher( line ) ).matches() )
                {
                    arch = Architecture.getByValue( matcher.group( 1 ) );
                }
            }
        }

        final String fname = name;
        final String fversion = version;
        final Architecture farch = arch;

        return new SubutaiTemplateMetadata()
        {

            @Override
            public Architecture getArchitecture()
            {
                return farch;
            }


            @Override
            public byte[] getMd5Sum()
            {
                return md5;
            }


            @Override
            public String getName()
            {
                return fname;
            }


            @Override
            public String getVersion()
            {
                return fversion;
            }
        };
    }


}

