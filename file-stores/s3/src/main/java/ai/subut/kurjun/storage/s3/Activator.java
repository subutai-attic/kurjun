package ai.subut.kurjun.storage.s3;


import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;

import ai.subut.kurjun.model.storage.FileStore;


/**
 * OSGi bundle activator class for S3 file store service.
 *
 */
public class Activator implements BundleActivator, ManagedService
{

    private static final Logger LOGGER = LoggerFactory.getLogger( Activator.class );

    private ServiceRegistration managedService;
    private ServiceRegistration s3Service;
    private BundleContext context;


    @Override
    public void start( BundleContext context ) throws Exception
    {
        this.context = context;

        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put( Constants.SERVICE_PID, ServiceConstants.SERVICE_PID );

        managedService = context.registerService( ManagedService.class, this, properties );
    }


    @Override
    public void stop( BundleContext context ) throws Exception
    {
        if ( s3Service != null )
        {
            s3Service.unregister();
        }
        managedService.unregister();
    }


    @Override
    public void updated( Dictionary<String, ?> config ) throws ConfigurationException
    {
        if ( config == null )
        {
            LOGGER.warn( "No config for S3 file store activator" );
            return;
        }
        String bucketName = ( String ) config.get( ServiceConstants.BUCKET_NAME );
        if ( bucketName == null || bucketName.isEmpty() )
        {
            throw new ConfigurationException( ServiceConstants.BUCKET_NAME, "must be specified" );
        }

        AWSCredentials credentials = null;
        String credentialsFile = ( String ) config.get( ServiceConstants.CREDENTIALS_FILE );
        if ( credentialsFile != null )
        {
            try
            {
                credentials = new PropertiesCredentials( new File( credentialsFile ) );
            }
            catch ( IOException | IllegalArgumentException ex )
            {
                throw new ConfigurationException( ServiceConstants.CREDENTIALS_FILE, "read failure", ex );
            }
        }
        else
        {
            String accessKey = ( String ) config.get( ServiceConstants.ACCESS_KEY );
            String secretKey = ( String ) config.get( ServiceConstants.SECRET_KEY );
            if ( accessKey == null || accessKey.isEmpty() )
            {
                throw new ConfigurationException( ServiceConstants.ACCESS_KEY, "must have value" );
            }
            if ( secretKey == null || secretKey.isEmpty() )
            {
                throw new ConfigurationException( ServiceConstants.SECRET_KEY, "must have value" );
            }
            credentials = new BasicAWSCredentials( accessKey, secretKey );
        }

        registerService( bucketName, credentials );
    }


    private void registerService( String bucketName, AWSCredentials credentials )
    {
        FileStore fs = new S3FileStore( bucketName, credentials );

        Dictionary serviceProperties = new Hashtable();
        serviceProperties.put( ServiceConstants.BUCKET_NAME, bucketName );

        s3Service = context.registerService( FileStore.class, fs, serviceProperties );
    }


}

