package ai.subut.kurjun.db.file.tests;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.apt.DefaultIndexPackageMetaData;
import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.metadata.common.raw.RawMetadata;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.model.index.TagItem;
import ai.subut.kurjun.model.metadata.Architecture;


/**
 * Generates random objects in the Kurjun model stores in mapdb.
 */
public class KurjunRandom
{
    public static String fingerprint()
    {
        return Hex.encodeHexString( sha1() );
    }


    private static byte[] hash( String algo )
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance( algo );
            return digest.digest( RandomStringUtils.randomAscii( 100 ).getBytes() );
        }
        catch ( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
        }

        return new byte[0];
    }


    public static byte[] md5() {
        return hash( "MD5" );
    }


    public static byte[] sha1()
    {
        return hash( "SHA-1" );
    }


    public static byte[] sha256()
    {
        return hash( "SHA-256" );
    }


    private static int randomInt( int start, int end )
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


    /**
     * Generates a random alphaNumeric name between 8 and 32 characters.
     * @return random name
     */
    public static String name()
    {
        return RandomStringUtils.randomAlphanumeric( randomInt( 8, 32 )  );
    }


    public static long size()
    {
        return Math.abs( RandomUtils.nextLong() ) ;
    }


    /**
     * Returns a random major, minor, micro version randomly.
     * @return version string
     */
    public static String version()
    {
        return String.valueOf( randomInt( 0, 100 ) )
                + '.' + String.valueOf( randomInt( 0, 100 ) )
                + '.' + String.valueOf( randomInt( 0, 100 ) );
    }


    public static String parent()
    {
        return "master";
    }


    public static String config()
    {
        return "Gets contents of the \"config\" file of this template.";
    }


    public static String packages()
    {
        return "Gets contents of the \"packages\" file of this template";
    }


    public static Map<String,String> extra()
    {
        return Collections.emptyMap();
    }


    public static List<TagItem> tag()
    {
        // List<TagItem> list = new ArrayList<>();
        return Collections.emptyList();
    }


    // ------------------------------------------------


    public static RawMetadata rawMetadata()
    {
        return new RawMetadata( md5(), name(), size(), fingerprint() );
    }


    public static DefaultTemplate defaultTemplate()
    {
        DefaultTemplate defaultTemplate = new DefaultTemplate();
        defaultTemplate.setArchitecture( Architecture.ALL );
        defaultTemplate.setName( name() );
        defaultTemplate.setId( fingerprint(), md5() );
        defaultTemplate.setOwnerFprint( fingerprint() );
        defaultTemplate.setMd5Sum( md5() );
        defaultTemplate.setSize( size() );
        defaultTemplate.setVersion( version() );
        defaultTemplate.setParent( parent() );
        defaultTemplate.setConfigContents( config() );
        defaultTemplate.setPackagesContents( packages() );
        defaultTemplate.setExtra( extra() );
        return defaultTemplate;
    }


    public static DefaultMetadata defaultMetadata()
    {
        DefaultMetadata defaultMetadata = new DefaultMetadata();
        defaultMetadata.setVersion( version() );
        defaultMetadata.setFingerprint( fingerprint() );
        defaultMetadata.setMd5sum( md5() );
        defaultMetadata.setName( name() );
        return defaultMetadata;
    }


    public static DefaultIndexPackageMetaData defaultIndexPackageMetaData()
    {
        DefaultIndexPackageMetaData defaultIndexPackageMetaData = new DefaultIndexPackageMetaData();

        defaultIndexPackageMetaData.setSize( size() );
        defaultIndexPackageMetaData.setSha1( sha1() );
        defaultIndexPackageMetaData.setSha256( sha256() );
        defaultIndexPackageMetaData.setDescriptionMd5( md5() );
        defaultIndexPackageMetaData.setTag( tag() );

        defaultPackageMetadata( defaultIndexPackageMetaData );

        return defaultIndexPackageMetaData;
    }


    public static DefaultPackageMetadata defaultPackageMetadata( DefaultIndexPackageMetaData defaultIndexPackageMetaData )
    {
        DefaultPackageMetadata defaultPackageMetadata;

        if ( defaultIndexPackageMetaData == null )
        {
            defaultPackageMetadata = new DefaultPackageMetadata();
        }
        else
        {
            defaultPackageMetadata = defaultIndexPackageMetaData;
        }

        defaultPackageMetadata.setExtra( extra() );
        defaultPackageMetadata.setVersion( version() );
        defaultPackageMetadata.setArchitecture( Architecture.ALL );
        defaultPackageMetadata.setMd5( md5() );
        defaultPackageMetadata.setInstalledSize( Math.abs( RandomUtils.nextInt() ) );
        // a lot more to do


        return defaultPackageMetadata;
    }
}
