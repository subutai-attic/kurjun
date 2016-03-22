package ai.subut.kurjun.riparser;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.riparser.service.ReleaseIndexParser;


/**
 * Guice module to initialize release index parser bindings.
 *
 */
public class ReleaseIndexParserModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( ReleaseIndexParser.class ).to( ReleaseIndexParserImpl.class );
    }

}

