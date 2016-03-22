package ai.subut.kurjun.snap;


import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import ai.subut.kurjun.model.metadata.snap.SnapMetadata;


public class SnapMetadataParserImplTest
{

    private SnapMetadataParserImpl parser;
    private InputStream packageMetadataStream;


    @Before
    public void setUp()
    {
        Yaml yaml = new SnapMetadataParserModule().makeYamlParser();
        parser = new SnapMetadataParserImpl( yaml );
        packageMetadataStream = ClassLoader.getSystemResourceAsStream( "package.yaml" );
    }


    @After
    public void tearDown() throws IOException
    {
        if ( packageMetadataStream != null )
        {
            packageMetadataStream.close();
        }
    }


    @Test
    public void testParse_File() throws Exception
    {
        // TODO: parse with snap package
    }


    @Test
    public void testParseMetadata() throws Exception
    {
        SnapMetadata m = parser.parseMetadata( packageMetadataStream );

        Assert.assertEquals( "go-example-webserver", m.getName() );
        Assert.assertEquals( "1.0.1", m.getVersion() );
        Assert.assertEquals( "Alexander Sack <asac@canonical.com>", m.getVendor() );
    }

}

