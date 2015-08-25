package ai.subut.kurjun.snap.metadata.store;


import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import ai.subut.kurjun.db.file.FileDbModule;
import ai.subut.kurjun.model.metadata.snap.SnapMetadataStore;


/**
 * OSGi activator for snap packages store. Configuration admin shall provide a file path where metadata will be stored.
 *
 */
public class Activator implements BundleActivator, ManagedService
{
    private BundleContext context;
    private ServiceRegistration metadataStoreReg;


    @Override
    public void start( BundleContext context ) throws Exception
    {
        this.context = context;

        Dictionary properties = new Properties();
        properties.put( Constants.SERVICE_PID, Activator.class.getName() );
        context.registerService( ManagedService.class, this, properties );
    }


    @Override
    public void stop( BundleContext context ) throws Exception
    {
        if ( metadataStoreReg != null )
        {
            metadataStoreReg.unregister();
            metadataStoreReg = null;
        }
    }


    @Override
    public void updated( Dictionary<String, ?> properties ) throws ConfigurationException
    {
        String keyFilePath = FileDbModule.DB_FILE_PATH;
        if ( properties != null )
        {
            String filePath = ( String ) properties.get( keyFilePath );
            if ( filePath == null || filePath.isEmpty() )
            {
                throw new ConfigurationException( keyFilePath, "invalid file path" );
            }

            Dictionary prop = new Properties();
            prop.put( keyFilePath, filePath );

            metadataStoreReg = context.registerService( SnapMetadataStore.class, new SnapMetadataStoreImpl( filePath ),
                                                        prop );
        }
    }

}

