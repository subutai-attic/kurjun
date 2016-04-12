package ai.subut.kurjun.db.file.tests;


import org.junit.Assert;
import org.junit.Test;

import org.apache.commons.codec.binary.Hex;

import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.apt.DefaultIndexPackageMetaData;
import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.metadata.common.raw.RawMetadata;
import ai.subut.kurjun.metadata.common.snap.DefaultSnapMetadata;
import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;


/**
 * Document me!
 */
public class KurjunRandomTest
{
    @Test
    public void testHashing() throws Exception
    {
        byte[] hash = KurjunRandom.md5();
        System.out.println( "md5 = " + Hex.encodeHexString( hash ) );

        hash = KurjunRandom.sha1();
        System.out.println( "sha1 = " + Hex.encodeHexString( hash ) );

        hash = KurjunRandom.sha256();
        System.out.println( "sha256 = " + Hex.encodeHexString( hash ) );

        System.out.println( "fingerprint = " + KurjunRandom.fingerprint() );
    }


    @Test
    public void testMetadata() throws Exception
    {
        //        DefaultMetadata defaultMetadata = KurjunRandom.defaultMetadata();
        //        Assert.assertNotNull( defaultMetadata );
        //        System.out.println( "defaultMetadata = " + defaultMetadata.serialize() );
        //
        //        DefaultIndexPackageMetaData defaultIndexPackageMetaData = KurjunRandom.defaultIndexPackageMetaData();
        //        Assert.assertNotNull( defaultIndexPackageMetaData );
        //        System.out.println( "defaultIndexPackageMetaData = " + defaultIndexPackageMetaData.serialize() );
        //
        //        DefaultTemplate defaultTemplate = KurjunRandom.defaultTemplate();
        //        Assert.assertNotNull( defaultTemplate );
        //        System.out.println( "defaultTemplate = " + defaultTemplate.serialize() );
        //
        //        DefaultPackageMetadata defaultPackageMetadata = KurjunRandom.defaultPackageMetadata( null );
        //        Assert.assertNotNull( defaultPackageMetadata );
        //        System.out.println( "defaultPackageMetadata = " + defaultPackageMetadata.serialize() );
        //
        //        RawMetadata rawMetadata = KurjunRandom.rawMetadata();
        //        Assert.assertNotNull( rawMetadata );
        //        System.out.println( "rawMetadata = " + rawMetadata.serialize() );

        RandomMetaData metaData = new RandomMetaData();

        DefaultMetadata defaultMetadata = KurjunRandom.defaultMetadata();
        Assert.assertNotNull( defaultMetadata );
        System.out.println( "defaultMetadata = " + defaultMetadata.serialize() );

        DefaultIndexPackageMetaData defaultIndexPackageMetaData = KurjunRandom.defaultIndexPackageMetaData();
        Assert.assertNotNull( defaultIndexPackageMetaData );
        System.out.println( "defaultIndexPackageMetaData = " + defaultIndexPackageMetaData.serialize() );

        DefaultTemplate defaultTemplate = metaData.generateTemplateMeta();
        Assert.assertNotNull( defaultTemplate );
        System.out.println( "defaultTemplate = " + defaultTemplate.serialize() );

        DefaultPackageMetadata defaultPackageMetadata = metaData.generatePackageMetaData();
        Assert.assertNotNull( defaultPackageMetadata );
        System.out.println( "defaultPackageMetadata = " + defaultPackageMetadata.serialize() );

        RawMetadata rawMetadata = metaData.generateRawMetaData();
        Assert.assertNotNull( rawMetadata );
        System.out.println( "rawMetadata = " + rawMetadata.serialize() );

        DefaultSnapMetadata snapMetadata = metaData.generateSnapMetaData();
        Assert.assertNotNull( snapMetadata );
        System.out.println( "rawMetadata = " + snapMetadata.serialize() );
    }
}
