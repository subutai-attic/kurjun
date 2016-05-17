package ai.subut.kurjun.db.file.tests;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.io.output.StringBuilderWriter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.apache.commons.io.FileUtils;

import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


public class DbTest
{
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private static final int TOTAL = 50;
    private static final int MYTHREADS = 10;
    private static final String[] MAP_NAMES = { "rawFiles", "subutaiTemplates", "aptPackages" };

    FileDb fileDb;


    @Before
    public void setUp() throws Exception
    {
        fileDb = new FileDb( "/tmp/var/lib/kurjun/fs/storage/" );
    }


    @After
    public void tearDown() throws Exception
    {
        fileDb.close();
    }


    // test save random types of metadata
    @Test
    public void testStressSaving() throws Exception
    {
        for ( int i = 0; i < TOTAL; i++ )
        {
            MetadataEnum type = MetadataEnum.getRandom();
            SerializableMetadata serializableMetadata = type.getSupplier().get();

            fileDb.put( type.getMapName(), serializableMetadata.getId(), serializableMetadata );
        }
    }


    // test getting metadata
    @Test
    public void testStressGettingMetaData()
    {
        for ( final MetadataEnum metadataEnum : MetadataEnum.values() )
        {
            fileDb.get( metadataEnum.getMapName() );
        }
    }
}
