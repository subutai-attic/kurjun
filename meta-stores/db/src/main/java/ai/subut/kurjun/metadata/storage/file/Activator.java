package ai.subut.kurjun.metadata.storage.file;


import java.io.IOException;
import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import ai.subut.kurjun.model.metadata.PackageMetadataStore;


public class Activator implements BundleActivator, ManagedService
{
    private BundleContext context;
    private ServiceRegistration managedService;
    private ServiceRegistration dbFileStoreService;


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
        if ( dbFileStoreService != null )
        {
            dbFileStoreService.unregister();
        }
        managedService.unregister();
    }


    @Override
    public void updated( Dictionary<String, ?> config ) throws ConfigurationException
    {
        if ( config != null )
        {
            String location = ( String ) config.get( ServiceConstants.FILE_LOCATION );
            if ( location == null || location.isEmpty() )
            {
                throw new ConfigurationException( ServiceConstants.FILE_LOCATION, "invalid location" );
            }

            Dictionary properties = new Properties();
            properties.put( ServiceConstants.FILE_LOCATION, location );

            DbFilePackageMetadataStore pms;
            try
            {
                pms = new DbFilePackageMetadataStore( location );
            }
            catch ( IOException ex )
            {
                throw new ConfigurationException( ServiceConstants.FILE_LOCATION, "Invalid location", ex );
            }

            dbFileStoreService = context.registerService( PackageMetadataStore.class, pms, properties );
        }
    }

}

