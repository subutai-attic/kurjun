package ai.subut.kurjun.common.utils;


import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


/**
 * Internet related utilities like IP, sockets, DNS, ping, etc.
 */
public class InetUtils
{

    private InetUtils()
    {
        // not to be constructed
    }


    /**
     * Gets local IPv4 addresses of the local host.
     *
     * @return list of IPv4 address of the local host
     * @throws SocketException
     */
    public static List<InetAddress> getLocalIPAddresses() throws SocketException
    {
        List<InetAddress> result = new ArrayList<>();

        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while ( interfaces.hasMoreElements() )
        {
            NetworkInterface ni = interfaces.nextElement();

            // skip down, loopback, and virtual interfaces
            if ( ( !ni.isUp() || ni.isLoopback() ) || ni.isVirtual() )
            {
                continue;
            }

            Enumeration<InetAddress> ips = ni.getInetAddresses();
            while ( ips.hasMoreElements() )
            {
                InetAddress ip = ips.nextElement();
                if ( ip instanceof Inet4Address )
                {
                    result.add( ( Inet4Address ) ip );
                }
            }
        }
        return result;
    }
    

    /**
     * Checks whether the given host and port is reachable or not. Tries to open a
     * socket with the given connection timeout.
     * @param host
     * @param port
     * @param connTimeout
     * @return 
     */
    public static boolean isHostReachable( String host, int port, int connTimeout )
    {
        Socket socket = null;
        boolean reachable = false;
        try
        {
            socket = new Socket();
            socket.connect( new InetSocketAddress( host, port ), connTimeout );
            reachable = true;
        }
        catch ( IOException ioe )
        {
        }
        finally
        {
            if ( socket != null )
            {
                try
                {
                    socket.close();
                }
                catch ( IOException e )
                {
                }
            }
        }
        return reachable;
    }


}

