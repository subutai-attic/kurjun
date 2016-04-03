package ai.subut.kurjun.subutai;


import com.google.inject.AbstractModule;

import ai.subut.kurjun.subutai.service.SubutaiTemplateParser;


/**
 * Guice module to initialize Subutai metadata parser bindings.
 *
 */
public class SubutaiTemplateParserModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( SubutaiTemplateParser.class ).to( SubutaiTemplateParserImpl.class );
    }

}

