package ai.subut.kurjun.subutai;


import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import ai.subut.kurjun.subutai.service.SubutaiTemplateParser;


/**
 * OSGi bundle activator.
 *
 */
public class Activator implements BundleActivator
{

    @Override
    public void start( BundleContext context ) throws Exception
    {
        Dictionary properties = new Properties();
        context.registerService( SubutaiTemplateParser.class, new SubutaiTemplateParserImpl(), properties );
    }


    @Override
    public void stop( BundleContext context ) throws Exception
    {
    }

}

