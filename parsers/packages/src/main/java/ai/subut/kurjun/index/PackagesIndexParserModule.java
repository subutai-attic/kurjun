package ai.subut.kurjun.index;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.index.service.PackagesIndexParser;


/**
 * Guice module to initialize packages index file parser.
 *
 */
public class PackagesIndexParserModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( PackagesIndexParser.class ).to( PackagesIndexParserImpl.class );
    }

}

