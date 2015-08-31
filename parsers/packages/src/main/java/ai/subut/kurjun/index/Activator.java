package ai.subut.kurjun.index;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import ai.subut.kurjun.index.service.PackagesIndexParser;


/**
 * OSGi bundle activator class for packages index builder and parser services.
 */
public class Activator implements BundleActivator
{


    @Override
    public void start( BundleContext context ) throws Exception
    {
        context.registerService( PackagesIndexParser.class, new PackagesIndexParserImpl(), null );
    }


    @Override
    public void stop( BundleContext context ) throws Exception
    {
    }

}

