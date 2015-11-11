package ai.subut.kurjun.subutai;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import ai.subut.kurjun.ar.DefaultTar;
import ai.subut.kurjun.ar.Tar;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.subutai.service.SubutaiTemplateParser;
import ai.subut.kurjun.subutai.service.TemplateProperties;


class SubutaiTemplateParserImpl implements SubutaiTemplateParser
{


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
        // Subutai config files complies to standard java properties file, so reading it as properties
        Properties prop = new Properties();
        try ( BufferedReader br = new BufferedReader( new InputStreamReader( stream ) ) )
        {
            prop.load( br );
        }

        return new SubutaiTemplateMetadata()
        {

            @Override
            public Architecture getArchitecture()
            {
                String a = prop.getProperty( TemplateProperties.ARCH );
                return Architecture.getByValue( a );
            }


            @Override
            public byte[] getMd5Sum()
            {
                return md5;
            }


            @Override
            public String getName()
            {
                return prop.getProperty( TemplateProperties.NAME );
            }


            @Override
            public String getVersion()
            {
                return prop.getProperty( TemplateProperties.VERSION );
            }


            @Override
            public Map<String, String> getExtra()
            {
                Set<String> skipProperties = new HashSet<>();
                skipProperties.add( TemplateProperties.NAME );
                skipProperties.add( TemplateProperties.VERSION );
                skipProperties.add( TemplateProperties.ARCH );

                Map< String, String> map = new HashMap<>();
                for ( String key : prop.stringPropertyNames() )
                {
                    if ( !skipProperties.contains( key ) )
                    {
                        map.put( key, prop.getProperty( key ) );
                    }
                }
                return map;
            }
        };
    }


}

