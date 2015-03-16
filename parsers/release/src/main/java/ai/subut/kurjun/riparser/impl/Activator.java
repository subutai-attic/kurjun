package ai.subut.kurjun.riparser.impl;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import ai.subut.kurjun.riparser.ReleaseIndexParser;


/**
 * OSGi bundle activator class for release index parser.
 */
public class Activator implements BundleActivator
{
    private ServiceRegistration<ReleaseIndexParser> service;


    @Override
    public void start( BundleContext context ) throws Exception
    {
        service = context.registerService( ReleaseIndexParser.class, new ReleaseIndexParserImpl(), null );
    }


    @Override
    public void stop( BundleContext context ) throws Exception
    {
        service.unregister();
    }

}

