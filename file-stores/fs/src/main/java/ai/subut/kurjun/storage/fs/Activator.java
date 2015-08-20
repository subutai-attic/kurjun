package ai.subut.kurjun.storage.fs;


import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import ai.subut.kurjun.model.storage.FileStore;


/**
 * OSGi bundle activator for FS file store service.
 *
 */
public class Activator implements BundleActivator, ManagedService
{
    private BundleContext context;
    private ServiceRegistration managedService;
    private ServiceRegistration fsService;


    @Override
    public void start( BundleContext context ) throws Exception
    {
        this.context = context;

        Dictionary properties = new Hashtable();
        properties.put( Constants.SERVICE_PID, ServiceConstants.SERVICE_PID );

        managedService = context.registerService( ManagedService.class, this, properties );
    }


    @Override
    public void stop( BundleContext context ) throws Exception
    {
        if ( fsService != null )
        {
            fsService.unregister();
        }
        managedService.unregister();
    }


    @Override
    public void updated( Dictionary<String, ?> config ) throws ConfigurationException
    {
        if ( config == null )
        {
            throw new ConfigurationException( "Properties", "must not be null" );
        }
        String location = ( String ) config.get( ServiceConstants.LOCATION );
        if ( location == null || location.isEmpty() )
        {
            throw new ConfigurationException( ServiceConstants.LOCATION, "must be a valid file system location" );
        }

        FileSystemFileStore fs = new FileSystemFileStore( location );

        Dictionary properties = new Hashtable();
        properties.put( ServiceConstants.LOCATION, location );
        fsService = context.registerService( FileStore.class, fs, properties );
    }

}

