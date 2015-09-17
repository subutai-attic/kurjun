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

import org.apache.commons.io.FileUtils;

import ai.subut.kurjun.ar.DefaultTar;
import ai.subut.kurjun.ar.Tar;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.template.TemplateMetadata;
import ai.subut.kurjun.subutai.service.SubutaiTemplateParser;


class SubutaiTemplateParserImpl implements SubutaiTemplateParser
{

    public static final Pattern NAME_LINE_PATTERN = Pattern.compile( "lxc.utsname\\s*=\\s*(.+)" );
    public static final Pattern ARCH_LINE_PATTERN = Pattern.compile( "lxc.arch\\s*=\\s*(.+)" );
    public static final Pattern VERSION_LINE_PATTERN = Pattern.compile( "subutai.template.version\\s*=\\s*(.+)" );


    @Override
    public TemplateMetadata parseTemplate( File file ) throws IOException
    {
        Path targetDir = Files.createTempDirectory( null );
        try
        {
            Tar tar = new DefaultTar( file );
            tar.extract( targetDir.toFile() );

            Path configPath = targetDir.resolve( "config" );
            try ( InputStream is = new FileInputStream( configPath.toFile() ) )
            {
                return parseTemplateConfigFile( is );
            }
        }
        finally
        {
            FileUtils.deleteDirectory( targetDir.toFile() );
        }
    }


    @Override
    public TemplateMetadata parseTemplateConfigFile( InputStream stream ) throws IOException
    {
        DefaultTemplateMetadata meta = new DefaultTemplateMetadata();
        try ( BufferedReader br = new BufferedReader( new InputStreamReader( stream ) ) )
        {
            String line;
            while ( ( line = br.readLine() ) != null )
            {
                Matcher matcher = NAME_LINE_PATTERN.matcher( line );
                if ( matcher.matches() )
                {
                    meta.setName( matcher.group( 1 ) );
                }
                else if ( ( matcher = VERSION_LINE_PATTERN.matcher( line ) ).matches() )
                {
                    meta.setVersion( matcher.group( 1 ) );
                }
                else if ( ( matcher = ARCH_LINE_PATTERN.matcher( line ) ).matches() )
                {
                    Architecture arch = Architecture.getByValue( matcher.group( 1 ) );
                    meta.setArchitecture( arch );
                }
            }
        }
        return meta;
    }

}

