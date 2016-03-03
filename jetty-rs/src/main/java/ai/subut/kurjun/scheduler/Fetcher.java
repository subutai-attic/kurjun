package ai.subut.kurjun.scheduler;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.subut.kurjun.rest.template.TemplateManagerStandalone;

import static java.util.concurrent.TimeUnit.MINUTES;


public class Fetcher
{

    private static final Logger LOGGER = LoggerFactory.getLogger( Fetcher.class );

    private final TemplateManagerStandalone templateManager;

    private ScheduledExecutorService metadataCacheUpdater;


    public Fetcher( final TemplateManagerStandalone templateManager )
    {

        this.templateManager = templateManager;

        LOGGER.debug( "Initializing fetcher..." );

        init();
    }


    public void init()
    {
        metadataCacheUpdater = Executors.newScheduledThreadPool( 1 );
    }


    public void fetch()
    {
        final Runnable fetcher = () -> {
            try
            {
                templateManager.addRemoteRepository( new URL( "" ), null );
            }
            catch ( MalformedURLException e )
            {
                e.printStackTrace();
            }
        };
        final ScheduledFuture<?> fetcherHandler = metadataCacheUpdater.scheduleAtFixedRate( fetcher, 10, 10, MINUTES );


    }


    public void dispose()
    {
        if ( !metadataCacheUpdater.isShutdown() )
        {
            metadataCacheUpdater.shutdown();
        }
    }


}
