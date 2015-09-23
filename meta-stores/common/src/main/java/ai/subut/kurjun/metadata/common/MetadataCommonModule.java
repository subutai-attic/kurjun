package ai.subut.kurjun.metadata.common;


import com.google.gson.Gson;
import com.google.inject.AbstractModule;

import ai.subut.kurjun.metadata.common.utils.MetadataUtils;


/**
 * Guice module to initialize common metadata specific bindings like JSON serializers.
 *
 */
public class MetadataCommonModule extends AbstractModule
{

    @Override
    protected void configure()
    {

        bind( Gson.class ).toInstance( MetadataUtils.JSON );

    }

}

