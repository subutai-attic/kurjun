package ai.subut.kurjun.web.torrent.tracker;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

import com.google.inject.Inject;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;

import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.common.service.KurjunProperties;


public class KurjunTracker
{

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger( KurjunTracker.class );

    @Inject
    KurjunProperties kurjunProperties;


    public Tracker getTracker() throws Exception
    {
        LOGGER.debug( "Creating new Tracker" );


        Tracker tracker = null;

        String torrentFilesDir = kurjunProperties.get( KurjunConstants.KURJUN_TORRENT_TRACKER_DIR );

        String torrentPort = kurjunProperties.get( KurjunConstants.KURJUN_TORRENT_PORT );

        if ( torrentFilesDir != null )
        {
            try
            {
                InetSocketAddress inetSocketAddress = new InetSocketAddress( Integer.parseInt( torrentPort ) );

                tracker = new Tracker( inetSocketAddress );

                FilenameFilter filter = ( dir, name ) -> name.endsWith( ".torrent" );

                for ( File file : new File( torrentFilesDir ).listFiles( filter ) )
                {
                    tracker.announce( TrackedTorrent.load( file ) );
                }
            }
            catch ( IOException | NoSuchAlgorithmException e )
            {
                e.printStackTrace();
            }
        }
        else
        {
            throw new Exception( "Trackers not found" );
        }

        return tracker;
    }
}
