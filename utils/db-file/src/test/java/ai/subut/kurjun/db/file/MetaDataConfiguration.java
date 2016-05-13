package ai.subut.kurjun.db.file;


import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

import com.google.common.collect.Lists;

import ai.subut.kurjun.model.metadata.snap.Framework;


public class MetaDataConfiguration
{
    public static String config()
    {
        return "Gets contents of the \"config\" file of this template.";
    }


    public static String packages()
    {
        return "Gets contents of the \"packages\" file of this template";
    }


    // generates size 1 - 100 mb
    public static long size()
    {
        long x = 1000000;
        long y = 100000000;
        Random r = new Random();

        return x + ( ( long ) ( r.nextDouble() * ( y - x ) ) );
    }


    public static String fingerprint()
    {
        return Hex.encodeHexString( sha1().getBytes() );
    }


    private static String hash( String algo )
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance( algo );
            return new String(digest.digest( RandomStringUtils.randomAscii( 100 ).getBytes() ));
        }
        catch ( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
        }

        return "";
    }


    public static String md5()
    {
        return hash( "MD5" );
    }


    private static String sha1()
    {
        return hash( "SHA-1" );
    }


    public static String sha256()
    {
        return hash( "SHA-256" );
    }


    public static Map<String, String> extra()
    {
        return Collections.emptyMap();
    }


    public static String name()
    {
        return RandomStringUtils.randomAlphanumeric( randomInt( 8, 32 ) );
    }


    public static int randomInt( int start, int end )
    {
        int value;

        if ( start < 0 || end < 2 || start >= end )
        {
            throw new IllegalArgumentException( "start = " + start + ", end = " + end );
        }

        do
        {
            value = RandomUtils.nextInt() % end;
        }
        while ( value > end || value < start );
        return value;
    }


    public static URL url() throws MalformedURLException
    {
        return new URL( "http://test.com" );
    }


    public static List<Framework> frameworks()
    {
        return Lists.newArrayList();
    }
}
