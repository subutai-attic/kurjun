package ai.subut.kurjun.snap;


import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import ai.subut.kurjun.snap.service.SnappyMetadataParser;


public class SnappyMetadataParserModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( SnappyMetadataParser.class ).to( SnappyMetadataParserImpl.class );
    }


    @Provides
    Yaml makeYamlParser()
    {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties( true );
        return new Yaml( new Constructor(), representer );
    }

}

