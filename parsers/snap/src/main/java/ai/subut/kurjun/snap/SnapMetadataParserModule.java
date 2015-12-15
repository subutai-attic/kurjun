package ai.subut.kurjun.snap;


import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import ai.subut.kurjun.snap.service.SnapMetadataParser;


/**
 * Guice module to initialize snap metadata parser bindings.
 *
 */
public class SnapMetadataParserModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( SnapMetadataParser.class ).to( SnapMetadataParserImpl.class );
    }


    @Provides
    public Yaml makeYamlParser()
    {
        // make yaml parser ignore missing fields instead of throwing exceptions
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties( true );

        return new Yaml( new Constructor(), representer );
    }

}

