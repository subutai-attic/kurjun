package ai.subut.kurjun.metadata.storage.nosql;


import java.io.IOException;
import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.subut.kurjun.model.metadata.PackageMetadataStore;


public class Activator implements BundleActivator, ManagedService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( Activator.class );

    private BundleContext context;
    private ServiceRegistration managedService;
    private ServiceRegistration<PackageMetadataStore> nosqlStoreService;


    @Override
    public void start( BundleContext context ) throws Exception
    {
        this.context = context;

        Dictionary properties = new Properties();
        properties.put( Constants.SERVICE_PID, ServiceConstants.SERVICE_PID );

        managedService = context.registerService( ManagedService.class, this, properties );
    }


    @Override
    public void stop( BundleContext context ) throws Exception
    {
        if ( nosqlStoreService != null )
        {
            nosqlStoreService.unregister();
        }
        managedService.unregister();
    }


    @Override
    public void updated( Dictionary<String, ?> config ) throws ConfigurationException
    {
        String node = ( String ) config.get( ServiceConstants.NODE );
        if ( node == null || node.isEmpty() )
        {
            throw new ConfigurationException( ServiceConstants.NODE, "invalid node" );
        }

        Integer port = ( Integer ) config.get( ServiceConstants.PORT );
        if ( port == null || port <= 0 )
        {
            throw new ConfigurationException( ServiceConstants.PORT, "invalid port" );
        }

        try
        {
            PackageMetadataStore pms = new NoSqlPackageMetadataStore( node, port );
            nosqlStoreService = context.registerService( PackageMetadataStore.class, pms, null );
        }
        catch ( IOException ex )
        {
            LOGGER.error( "Failed to init NoSQL package metadata store", ex );
        }
    }

}

