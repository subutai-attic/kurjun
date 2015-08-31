package ai.subut.kurjun.metadata.common;


import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.inject.AbstractModule;

import ai.subut.kurjun.model.metadata.Dependency;


/**
 * Guice module to initialize common metadata specific bindings like JSON serializers.
 *
 */
public class MetadataCommonModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        GsonBuilder gb = new GsonBuilder().setPrettyPrinting();
        InstanceCreator<Dependency> depInstanceCreator = new InstanceCreator<Dependency>()
        {
            @Override
            public Dependency createInstance( Type type )
            {
                return new DefaultDependency();
            }
        };
        gb.registerTypeAdapter( Dependency.class, depInstanceCreator );

        bind( Gson.class ).toInstance( gb.create() );

    }

}

