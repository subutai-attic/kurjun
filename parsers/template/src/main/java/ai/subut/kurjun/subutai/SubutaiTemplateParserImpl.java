package ai.subut.kurjun.subutai;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import ai.subut.kurjun.ar.DefaultTar;
import ai.subut.kurjun.ar.Tar;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.subutai.service.SubutaiTemplateParser;
import ai.subut.kurjun.subutai.service.TemplateProperties;


class SubutaiTemplateParserImpl implements SubutaiTemplateParser
{

    static final String MD5_KEY = "x-md5";
    static final String CONFIG_FILE_CONTENTS_KEY = "x-config";
    static final String PACKAGES_FILE_CONTENTS_KEY = "x-packages";


    @Override
    public SubutaiTemplateMetadata parseTemplate( File file ) throws IOException
    {
        byte[] md5;
        try ( InputStream is = new FileInputStream( file ) )
        {
            //buffered digest
            md5 = DigestUtils.md5( is );
        }

        Path targetDir = Files.createTempDirectory( null );
        try
        {
            Tar tar = new DefaultTar( file );
            tar.extract( targetDir.toFile() );

            Path configPath = targetDir.resolve( "config" );
            Path packagesPath = targetDir.resolve( "packages" );
            try ( InputStream cis = new FileInputStream( configPath.toFile() );
                  InputStream pis = new FileInputStream( packagesPath.toFile() ) )
            {
                return parseConfigFile( cis, pis, md5 );
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
        return parseConfigFile( stream, null, null );
    }


    private SubutaiTemplateMetadata parseConfigFile( InputStream stream, InputStream packagesStream, byte[] md5 ) throws IOException
    {
        if ( stream == null )
        {
            throw new IOException( "Input stream is null." );
        }

        String txt = IOUtils.toString( stream );

        // Subutai config files complies to standard java properties file, so reading it as properties
        Properties prop = new Properties();
        prop.put( CONFIG_FILE_CONTENTS_KEY, txt );
        try ( Reader br = new InputStreamReader( IOUtils.toInputStream( txt ) ) )
        {
            prop.load( br );
        }
        if ( packagesStream != null )
        {
            prop.put( PACKAGES_FILE_CONTENTS_KEY, IOUtils.toString( packagesStream ) );
        }
        if ( md5 != null )
        {
            prop.put( MD5_KEY, md5 );
        }

        return makeMetadata( prop );
    }


    private SubutaiTemplateMetadata makeMetadata( Properties prop )
    {
        return new SubutaiTemplateMetadata()
        {

            @Override
            public String getId()
            {
                return Hex.encodeHexString( getMd5Sum() );
            }


            @Override
            public Architecture getArchitecture()
            {
                String a = prop.getProperty( TemplateProperties.ARCH );
                return Architecture.getByValue( a );
            }


            @Override
            public String getParent()
            {
                return prop.getProperty( TemplateProperties.PARENT );
            }


            @Override
            public String getPackage()
            {
                return prop.getProperty( TemplateProperties.PACKAGE );
            }


            @Override
            public byte[] getMd5Sum()
            {
                return ( byte[] ) prop.get( MD5_KEY );
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
            public String getConfigContents()
            {
                return prop.getProperty( CONFIG_FILE_CONTENTS_KEY );
            }


            @Override
            public String getPackagesContents()
            {
                return prop.getProperty( PACKAGES_FILE_CONTENTS_KEY );
            }


            @Override
            public String getOwnerFprint()
            {
                // there is no owner field in config file
                return null;
            }

            @Override
            public Map<String, String> getExtra()
            {
                Set<String> skipProperties = new HashSet<>();
                skipProperties.add( TemplateProperties.NAME );
                skipProperties.add( TemplateProperties.VERSION );
                skipProperties.add( TemplateProperties.ARCH );
                skipProperties.add( TemplateProperties.PARENT );
                skipProperties.add( TemplateProperties.PACKAGE );
                skipProperties.add( MD5_KEY );
                skipProperties.add( CONFIG_FILE_CONTENTS_KEY );
                skipProperties.add( PACKAGES_FILE_CONTENTS_KEY );

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

