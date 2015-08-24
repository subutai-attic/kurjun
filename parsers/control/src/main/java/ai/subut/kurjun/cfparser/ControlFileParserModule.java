package ai.subut.kurjun.cfparser;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.cfparser.service.ControlFileParser;


/**
 * Guice module to initialize control file parser bindings.
 *
 */
public class ControlFileParserModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( ControlFileParser.class ).to( DefaultControlFileParser.class );
    }

}

