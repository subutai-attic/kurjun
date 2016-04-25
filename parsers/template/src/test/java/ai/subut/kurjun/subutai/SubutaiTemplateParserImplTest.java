package ai.subut.kurjun.subutai;


import java.io.InputStream;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;
import ai.subut.kurjun.subutai.service.SubutaiTemplateParser;


public class SubutaiTemplateParserImplTest
{

    private SubutaiTemplateParser parser = new SubutaiTemplateParserImpl();


    @Test
    public void testParseTemplateConfigFile() throws Exception
    {
//        SubutaiTemplateMetadata metadata;
//        try ( InputStream is = ClassLoader.getSystemResourceAsStream( "config" ) )
//        {
//            Assume.assumeNotNull( is );
//            metadata = parser.parseTemplateConfigFile( is );
//        }
//
//        Assert.assertEquals( "hadoop", metadata.getName() );
//        Assert.assertEquals( "2.1.2", metadata.getVersion() );
//        Assert.assertEquals( Architecture.AMD64, metadata.getArchitecture() );
//        Assert.assertFalse( metadata.getExtra().isEmpty() );
    }

}

