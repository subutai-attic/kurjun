package ai.subut.kurjun.repo;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.riparser.service.ReleaseIndexParser;


public class Activator implements BundleActivator
{

    private static final Logger LOGGER = LoggerFactory.getLogger( Activator.class );


    @Override
    public void start( BundleContext context ) throws Exception
    {
        ServiceReference<ReleaseIndexParser> ref = context.getServiceReference( ReleaseIndexParser.class );
        if ( ref == null )
        {
            LOGGER.error( "Could not find release index parser service" );
            return;
        }

        ReleaseIndexParser releaseIndexParser = context.getService( ref );
        if ( releaseIndexParser == null )
        {
            LOGGER.error( "Failed to get release index parser service object" );
            return;
        }

        // TODO:
        LocalAptRepositoryImpl localRepo = new LocalAptRepositoryImpl( releaseIndexParser, "" );
        context.registerService( LocalRepository.class, localRepo, null );
    }


    @Override
    public void stop( BundleContext context ) throws Exception
    {
    }

}

