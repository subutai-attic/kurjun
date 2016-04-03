package ai.subut.kurjun.web.torrent;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

import com.google.inject.Inject;
import com.turn.ttorrent.common.Torrent;
import com.turn.ttorrent.tracker.Tracker;

import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.model.metadata.Metadata;


public class KurjunTorrent
{

    @Inject
    KurjunProperties kurjunProperties;


    /**
     * Create {@link Torrent} and save on file system .torrent file
     * .torrent file will be located {@link KurjunConstants.KURJUN_TORRENT_TRACKER_DIR}
     * @param source absolute path to a file to create a torrent from
     * @param tracker to announce to a torrent tracker
     * @param metadata to create filename from metadata id
     */
    public Torrent createTorrentFile( String source, Tracker tracker, Metadata metadata )
    {
        try
        {
            Torrent torrent =
                    Torrent.create( new File( source ), tracker.getAnnounceUrl().toURI(), getTorrentCreator() );

            FileOutputStream fileOutputStream =
                    new FileOutputStream( getTorrentOutputDir().concat( ( String ) metadata.getId() ) );

            torrent.save( fileOutputStream );

            fileOutputStream.close();

            return torrent;
        }
        catch ( InterruptedException | IOException | NoSuchAlgorithmException | URISyntaxException e )
        {
            e.printStackTrace();
        }
        return null;
    }


    public String getTorrentOutputDir()
    {
        return kurjunProperties.get( KurjunConstants.KURJUN_TORRENT_TRACKER_DIR );
    }


    public String getTorrentCreator()
    {
        return kurjunProperties.get( KurjunConstants.KURJUN_TORRENT_CREATOR );
    }
}
