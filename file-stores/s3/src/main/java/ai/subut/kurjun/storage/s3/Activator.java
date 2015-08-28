package ai.subut.kurjun.storage.s3;


import java.io.File;
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

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;

import ai.subut.kurjun.model.storage.FileStore;

import static ai.subut.kurjun.storage.s3.S3FileStoreModule.S3_ACCESS_KEY;
import static ai.subut.kurjun.storage.s3.S3FileStoreModule.S3_SECRET_KEY;


/**
 * OSGi bundle activator class for S3 file store service.
 *
 */
public class Activator implements BundleActivator, ManagedService
{

    public static final String SERVICE_PID = S3FileStore.class.getName();
    public static final String BUCKET_NAME = "file.store.s3.bucketName";
    public static final String CREDENTIALS_FILE = "file.store.s3.credentialsFile";

    private static final Logger LOGGER = LoggerFactory.getLogger( Activator.class );

    private ServiceRegistration managedService;
    private ServiceRegistration s3Service;
    private BundleContext context;


    @Override
    public void start( BundleContext context ) throws Exception
    {
        this.context = context;

        Dictionary properties = new Properties();
        properties.put( Constants.SERVICE_PID, SERVICE_PID );

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
        String bucketName = ( String ) config.get( BUCKET_NAME );
        if ( bucketName == null || bucketName.isEmpty() )
        {
            throw new ConfigurationException( BUCKET_NAME, "must be specified" );
        }

        AWSCredentials credentials = null;
        String credentialsFile = ( String ) config.get( CREDENTIALS_FILE );
        if ( credentialsFile != null )
        {
            try
            {
                credentials = new PropertiesCredentials( new File( credentialsFile ) );
            }
            catch ( IOException | IllegalArgumentException ex )
            {
                throw new ConfigurationException( CREDENTIALS_FILE, "read failure", ex );
            }
        }
        else
        {
            String accessKey = ( String ) config.get( S3_ACCESS_KEY );
            String secretKey = ( String ) config.get( S3_SECRET_KEY );
            if ( accessKey == null || accessKey.isEmpty() )
            {
                throw new ConfigurationException( S3_ACCESS_KEY, "must have value" );
            }
            if ( secretKey == null || secretKey.isEmpty() )
            {
                throw new ConfigurationException( S3_SECRET_KEY, "must have value" );
            }
            credentials = new BasicAWSCredentials( accessKey, secretKey );
        }

        registerService( bucketName, credentials );
    }


    private void registerService( String bucketName, AWSCredentials credentials )
    {
        FileStore fs = new S3FileStore( credentials, bucketName );

        Dictionary serviceProperties = new Properties();
        serviceProperties.put( BUCKET_NAME, bucketName );

        s3Service = context.registerService( FileStore.class, fs, serviceProperties );
    }


}

