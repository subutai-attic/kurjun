package ai.subut.kurjun.metadata.storage.sql;


import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import ai.subut.kurjun.model.metadata.PackageMetadataStore;


/**
 * OSGi bundle activator for SQL DB store for packages metadata.
 *
 */
public class Activator implements BundleActivator, ManagedService
{
    private BundleContext context;
    private ServiceRegistration managedService;
    private ServiceRegistration<PackageMetadataStore> sqldbService;


    @Override
    public void start( BundleContext context ) throws Exception
    {
        this.context = context;

        Dictionary properties = new Hashtable();
        properties.put( Constants.SERVICE_PID, ConnectionPropertyName.SERVICE_PID );

        managedService = context.registerService( ManagedService.class, this, properties );
    }


    @Override
    public void stop( BundleContext context ) throws Exception
    {
        if ( sqldbService != null )
        {
            sqldbService.unregister();
        }
        managedService.unregister();
    }


    @Override
    public void updated( Dictionary<String, ?> config ) throws ConfigurationException
    {
        if ( config.get( ConnectionPropertyName.DATASOURCE_CLASS_NAME ) == null )
        {
            throw new ConfigurationException( ConnectionPropertyName.DATASOURCE_CLASS_NAME,
                                              "invalid data source class name" );
        }
        if ( config.get( ConnectionPropertyName.DATASOURCE_SERVER_NAME ) == null )
        {
            throw new ConfigurationException( ConnectionPropertyName.DATASOURCE_SERVER_NAME, "invalid host" );
        }
        if ( config.get( ConnectionPropertyName.DATASOURCE_DATABASE_NAME ) == null )
        {
            throw new ConfigurationException( ConnectionPropertyName.DATASOURCE_DATABASE_NAME, "invalid database name" );
        }
        if ( config.get( ConnectionPropertyName.DATASOURCE_USER ) == null )
        {
            throw new ConfigurationException( ConnectionPropertyName.DATASOURCE_USER, "invalid username" );
        }
        if ( config.get( ConnectionPropertyName.DATASOURCE_PASS ) == null )
        {
            throw new ConfigurationException( ConnectionPropertyName.DATASOURCE_PASS, "invalid password" );
        }

        // dump all config to properties instance
        Properties properties = new Properties();
        Enumeration<String> en = config.keys();
        while ( en.hasMoreElements() )
        {
            String key = en.nextElement();
            properties.put( key, config.get( key ) );
        }

        SqlDbPackageMetadataStore pms = new SqlDbPackageMetadataStore();
        pms.init( properties );
        sqldbService = context.registerService( PackageMetadataStore.class, pms, null );
    }

}

