package ai.subut.kurjun.metadata.storage.file;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.apache.commons.codec.digest.DigestUtils;

import ai.subut.kurjun.metadata.common.DependencyImpl;
import ai.subut.kurjun.metadata.common.PackageMetadataImpl;
import ai.subut.kurjun.metadata.common.PackageMetadataListingImpl;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.Dependency;
import ai.subut.kurjun.model.metadata.PackageMetadata;
import ai.subut.kurjun.model.metadata.PackageMetadataListing;
import ai.subut.kurjun.model.metadata.Priority;
import ai.subut.kurjun.model.metadata.RelationOperator;


public class DbFilePackageMetadataStoreTest
{

    private static DbFilePackageMetadataStore store;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private PackageMetadata meta;
    private List<PackageMetadata> extraItems;
    private byte[] otherMd5;


    @Before
    public void setUp() throws IOException
    {
        store = new DbFilePackageMetadataStore( temporaryFolder.newFolder().toString() );
        meta = createPackageMetadata();
        store.put( meta );

        extraItems = new ArrayList<>();
        otherMd5 = DigestUtils.md5( "other content" );
    }


    @After
    public void tearDown()
    {
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
        PackageMetadata res = store.get( meta.getMd5Sum() );
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
            PackageMetadata pm = createPackageMetadata();
            store.put( pm );
            extraItems.add( pm );
        }

        PackageMetadataListing ls = store.list();

        Assert.assertNotNull( ls );
        Assert.assertTrue( ls.isTruncated() );
        Assert.assertEquals( store.batchSize, ls.getPackageMetadata().size() );

        PackageMetadataListing next = store.listNextBatch( ls );
        Assert.assertNotNull( next );
    }


    @Test( expected = IllegalStateException.class )
    public void testListNextBatchWithInvalidInput() throws Exception
    {
        PackageMetadataListingImpl listing = new PackageMetadataListingImpl();
        listing.setTruncated( false );

        store.listNextBatch( listing );
    }


    @Test( expected = IllegalStateException.class )
    public void testListNextBatchWithoutMarker() throws IOException
    {
        store.listNextBatch( new PackageMetadataListingImpl() );
    }


    private PackageMetadata createPackageMetadata()
    {
        PackageMetadataImpl pm = new PackageMetadataImpl();
        pm.setPackage( UUID.randomUUID().toString() );
        pm.setArchitecture( Architecture.amd64 );
        pm.setDescription( "Description here" );
        pm.setFilename( pm.getPackage() + "-ver-arch.deb" );
        pm.setInstalledSize( 1234 );
        pm.setMaintainer( "Maintainer" );
        pm.setMd5( DigestUtils.md5( pm.getFilename() ) );
        pm.setPriority( Priority.important );

        DependencyImpl dep = new DependencyImpl();
        dep.setPackage( "Package" );
        dep.setVersion( "1.0.0" );
        dep.setRelationOperator( RelationOperator.StrictlyLater );

        List<Dependency> ls = new ArrayList<>();
        ls.add( dep );
        pm.setDependencies( ls );

        return pm;
    }


}

