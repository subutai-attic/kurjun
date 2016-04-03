package ai.subut.kurjun.web.torrent.client;


import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;

import com.google.inject.Inject;
import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;

import ai.subut.kurjun.common.service.KurjunProperties;


public class KurjunClient
{
    @Inject
    KurjunProperties kurjunProperties;


    public Client createClient( File source, File output, InetAddress inetAddress )
    {
        try
        {
            SharedTorrent sharedTorrent = SharedTorrent.fromFile( source, output );
            return new Client( inetAddress, sharedTorrent );
        }
        catch ( IOException | NoSuchAlgorithmException e )
        {
            e.printStackTrace();
        }
        return null;
    }


    public Client appendObserver( Client client )
    {
        client.addObserver( ( o, arg ) -> {
            Client cl = ( Client ) o;
            float progress = cl.getTorrent().getCompletion();
        } );
        return client;
    }


    public String getAddress()
    {
        String addr = "";
        try
        {
            addr = InetAddress.getLocalHost().getHostAddress();
        }
        catch ( UnknownHostException e )
        {
            e.printStackTrace();
        }
        return addr;
    }
}
