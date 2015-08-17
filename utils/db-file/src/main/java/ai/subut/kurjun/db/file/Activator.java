package ai.subut.kurjun.db.file;


import java.io.IOException;
import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;


public class Activator implements BundleActivator, ManagedService
{
    private BundleContext context;
    private ServiceRegistration managedService;
    private ServiceRegistration fileDbService;


    @Override
    public void start( BundleContext context ) throws Exception
    {
        this.context = context;

        Dictionary properties = new Properties();
        properties.put( Constants.SERVICE_PID, FileDb.class.getName() );

        managedService = context.registerService( ManagedService.class, this, properties );
    }


    @Override
    public void stop( BundleContext context ) throws Exception
    {
        if ( fileDbService != null )
        {
            fileDbService.unregister();
        }
        managedService.unregister();
    }


    @Override
    public void updated( Dictionary<String, ?> properties ) throws ConfigurationException
    {
        if ( properties != null )
        {
            String filePath = ( String ) properties.get( FileDbModule.DB_FILE_PATH );
            if ( filePath == null || filePath.isEmpty() )
            {
                throw new ConfigurationException( FileDbModule.DB_FILE_PATH, "file path not specified" );
            }

            Dictionary p = new Properties();
            p.put( FileDbModule.DB_FILE_PATH, filePath );

            try
            {
                FileDb fileDb = new FileDb( filePath );
                fileDbService = context.registerService( FileDb.class, fileDb, p );
            }
            catch ( IOException ex )
            {
                throw new ConfigurationException( FileDbModule.DB_FILE_PATH, "file path", ex );
            }
        }
    }

}

