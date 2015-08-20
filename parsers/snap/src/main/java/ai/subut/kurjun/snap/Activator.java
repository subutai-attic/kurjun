package ai.subut.kurjun.snap;


import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.yaml.snakeyaml.Yaml;

import ai.subut.kurjun.snap.service.SnapMetadataParser;


/**
 * OSGi bundle activator for this module.
 *
 */
public class Activator implements BundleActivator
{

    @Override
    public void start( BundleContext context ) throws Exception
    {
        ServiceReference<Yaml> yamlRef = context.getServiceReference( Yaml.class );
        if ( yamlRef == null )
        {
            throw new IllegalStateException( "Yaml service reference not found" );
        }

        SnapMetadataParserImpl parser = new SnapMetadataParserImpl();
        parser.yaml = context.getService( yamlRef );

        Dictionary properties = new Properties();
        context.registerService(SnapMetadataParser.class, parser, properties );
    }


    @Override
    public void stop( BundleContext context ) throws Exception
    {
        // services automatically unregistered by framework
    }

}

