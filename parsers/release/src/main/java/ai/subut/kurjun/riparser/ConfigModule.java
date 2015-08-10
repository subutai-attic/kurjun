package ai.subut.kurjun.riparser;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.riparser.service.ReleaseIndexParser;



public class ConfigModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( ReleaseIndexParser.class ).to( ReleaseIndexParserImpl.class );
    }

}

