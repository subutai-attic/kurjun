package ai.subut.kurjun.metadata.storage.nosql;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.digest.DigestUtils;

import ai.subut.kurjun.metadata.common.MetadataListingImpl;
import ai.subut.kurjun.metadata.common.apt.DefaultDependency;
import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.MetadataListing;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.apt.Dependency;
import ai.subut.kurjun.model.metadata.apt.Priority;
import ai.subut.kurjun.model.metadata.apt.RelationOperator;


public class NoSqlPackageMetadataStoreTest
{

    private static final Logger LOGGER = LoggerFactory.getLogger( NoSqlPackageMetadataStoreTest.class );
    private static NoSqlPackageMetadataStore store;

    private SerializableMetadata meta;
    private List<SerializableMetadata> extraItems;
    private byte[] otherMd5;


    @BeforeClass
    public static void setUpClass() throws IOException
    {
        try ( InputStream is = ClassLoader.getSystemResourceAsStream( "cassandra.properties" ) )
        {
            Properties prop = new Properties();
            prop.load( is );
            store = new NoSqlPackageMetadataStore( prop.getProperty( "test.cassandra.node" ),
                                                   Integer.parseInt( prop.getProperty( "test.cassandra.port" ) ) );
        }
        catch ( Exception ex )
        {
            LOGGER.error( "Failed to initialize Cassandra connection", ex );
        }
    }


    @AfterClass
    public static void tearDownClass() throws IOException
    {
        CassandraConnector.getInstance().close();
    }


    @Before
    public void setUp() throws IOException
    {
        Assume.assumeNotNull( store );

        meta = createPackageMetadata();
        store.put( meta );

        extraItems = new ArrayList<>();
        otherMd5 = DigestUtils.md5( "other content" );
    }


    @After
    public void tearDown() throws IOException
    {
        if ( store != null )
        {
            store.remove( meta.getMd5Sum() );
            for ( SerializableMetadata item : extraItems )
            {
                store.remove( item.getMd5Sum() );
            }
        }
    }


    @Test
    public void testContains() throws Exception
    {
        Assert.assertTrue( store.contains( meta.getMd5Sum() ) );
        Assert.assertFalse( store.contains( otherMd5 ) );
    }


    @Test
    public void testGet() throws Exception
    {
        Metadata res = store.get( meta.getMd5Sum() );
        Assert.assertEquals( meta, res );
        Assert.assertNull( store.get( otherMd5 ) );
    }


    @Test
    public void testPut() throws Exception
    {
        // already exists
        Assert.assertFalse( store.put( meta ) );
    }


    @Test
    public void testRemove() throws Exception
    {
        // does not exist
        Assert.assertFalse( store.remove( otherMd5 ) );

        // removed first then does not exist anymore
        Assert.assertTrue( store.remove( meta.getMd5Sum() ) );
        Assert.assertFalse( store.remove( meta.getMd5Sum() ) );
    }


    @Test
    public void testList() throws Exception
    {
        store.batchSize = 10;

        // put twice of the batch size
        for ( int i = 0; i < store.batchSize * 2; i++ )
        {
            SerializableMetadata pm = createPackageMetadata();
            store.put( pm );
            extraItems.add( pm );
        }

        MetadataListing ls = store.list();

        Assert.assertNotNull( ls );
        Assert.assertTrue( ls.isTruncated() );
        Assert.assertEquals( store.batchSize, ls.getPackageMetadata().size() );

        MetadataListing next = store.listNextBatch( ls );
        Assert.assertNotNull( next );
    }


    @Test( expected = IllegalStateException.class )
    public void testListNextBatchWithInvalidInput() throws Exception
    {
        MetadataListingImpl listing = new MetadataListingImpl();
        listing.setTruncated( false );

        store.listNextBatch( listing );
    }


    @Test( expected = IllegalStateException.class )
    public void testListNextBatchWithoutMarker() throws IOException
    {
        store.listNextBatch( new MetadataListingImpl() );
    }


    private DefaultPackageMetadata createPackageMetadata()
    {
        DefaultPackageMetadata pm = new DefaultPackageMetadata();
        pm.setArchitecture( Architecture.AMD64 );
        pm.setDescription( "Description here" );
        pm.setFilename( UUID.randomUUID().toString() + "-ver-arch.deb" );
        pm.setInstalledSize( 1234 );
        pm.setMaintainer( "Maintainer" );
        pm.setMd5( DigestUtils.md5( pm.getFilename() ) );
        pm.setPriority( Priority.important );

        DefaultDependency dep = new DefaultDependency();
        dep.setPackage( "Package" );
        dep.setVersion( "1.0.0" );
        dep.setDependencyOperator( RelationOperator.StrictlyLater );

        List<Dependency> ls = new ArrayList<>();
        ls.add( dep );
        pm.setDependencies( ls );

        return pm;
    }
}

