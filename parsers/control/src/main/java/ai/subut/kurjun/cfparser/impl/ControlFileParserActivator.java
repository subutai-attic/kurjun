package ai.subut.kurjun.cfparser.impl;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import ai.subut.kurjun.cfparser.ControlFileParser;


public class ControlFileParserActivator implements BundleActivator
{
    private ServiceRegistration<ControlFileParser> service;


    @Override
    public void start( BundleContext context ) throws Exception
    {
        service = context.registerService( ControlFileParser.class, new DefaultControlFileParser(), null );
    }


    @Override
    public void stop( BundleContext context ) throws Exception
    {
        service.unregister();
    }

}

