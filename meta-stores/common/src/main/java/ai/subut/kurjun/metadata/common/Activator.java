package ai.subut.kurjun.metadata.common;


import java.lang.reflect.Type;
import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;

import ai.subut.kurjun.model.metadata.Dependency;


public class Activator implements BundleActivator
{

    @Override
    public void start( BundleContext context ) throws Exception
    {

        // build gson and resgiter it as a service
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

        Dictionary properties = new Properties();
        context.registerService( Gson.class, gb.create(), properties );


    }


    @Override
    public void stop( BundleContext context ) throws Exception
    {
    }

}

