package ai.subut.kurjun.db.file.tests;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.apache.commons.io.FileUtils;

import ai.subut.kurjun.db.file.FileDb;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


/**
 * Document me!
 */
public class DbTest
{
    public static final String TARGET_PATH_PROP = "target";
    public static final int TOTAL = 10000;
    public static final int CONCURRENCY = 200;

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();


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


    @Test
    public void testStress() throws Exception
    {
        for ( int i = 0; i < 10; i++ )
        {
            MetadataEnum type = MetadataEnum.getRandom();
            SerializableMetadata serializableMetadata = type.getSupplier().get();

            fileDb.put( type.getMapName(), serializableMetadata.getId(), serializableMetadata );
        }
    }
}
