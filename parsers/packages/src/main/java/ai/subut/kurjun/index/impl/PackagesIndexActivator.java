package ai.subut.kurjun.index.impl;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import ai.subut.kurjun.index.PackagesIndexBuilder;
import ai.subut.kurjun.index.PackagesIndexParser;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.storage.FileStore;


/**
 * OSGi bundle activator class for packages index builder and parser services.
 */
public class PackagesIndexActivator implements BundleActivator
{
    private ServiceRegistration<PackagesIndexParser> parserService;
    private ServiceRegistration<PackagesIndexBuilder> builderService;


    @Override
    public void start( BundleContext context ) throws Exception
    {
        parserService = context.registerService( PackagesIndexParser.class, new PackagesIndexParserImpl(), null );


        // get index builder dependencies
        PackageMetadataStore metadataStore = null;
        FileStore fileStore = null;

        ServiceReference<PackageMetadataStore> pmRef = context.getServiceReference( PackageMetadataStore.class );
        if ( pmRef != null )
        {
            metadataStore = context.getService( pmRef );
        }
        ServiceReference<FileStore> fsRef = context.getServiceReference( FileStore.class );
        if ( fsRef != null )
        {
            fileStore = context.getService( fsRef );
        }

        if ( metadataStore != null && fileStore != null )
        {
            PackagesIndexBuilder indexBuilder = new PackagesIndexBuilderImpl( metadataStore, fileStore );
            builderService = context.registerService( PackagesIndexBuilder.class, indexBuilder, null );
        }
    }


    @Override
    public void stop( BundleContext context ) throws Exception
    {
        parserService.unregister();
        builderService.unregister();
    }

}

