package ai.subut.kurjun.index.impl;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import ai.subut.kurjun.index.PackagesIndexBuilder;


public class PackagesIndexActivator implements BundleActivator
{
    private ServiceRegistration<PackagesIndexParserImpl> parserService;
    private ServiceRegistration<PackagesIndexBuilder> builderService;


    @Override
    public void start( BundleContext context ) throws Exception
    {
        parserService = context.registerService( PackagesIndexParserImpl.class, new PackagesIndexParserImpl(), null );
        builderService = context.registerService( PackagesIndexBuilder.class, new PackagesIndexBuilderImpl(), null );
    }


    @Override
    public void stop( BundleContext context ) throws Exception
    {
        parserService.unregister();
        builderService.unregister();
    }

}

