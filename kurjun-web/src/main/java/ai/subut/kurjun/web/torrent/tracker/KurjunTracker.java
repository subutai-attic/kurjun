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


    public Tracker initTracker() throws Exception
    {
        LOGGER.debug( "Creating new Tracker" );


        Tracker tracker = null;
        //dir to get all .torrent files
        String torrentFilesDir = kurjunProperties.get( KurjunConstants.KURJUN_TORRENT_TRACKER_DIR );
        //torrent default port
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
                    LOGGER.debug( "Announcing : {}", file.getAbsolutePath() );

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
