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
import org.junit.Test;

import org.apache.commons.io.FileUtils;

import ai.subut.kurjun.db.file.FileDb;


/**
 * Document me!
 */
public class FileDbStressTest
{
    public static final String TARGET_PATH_PROP = "target";
    public static final int TOTAL = 10000;
    public static final int CONCURRENCY = 200;

    File file;
    FileDb fileDb;
    ExecutorService workers;
    List<Future<Result>> results;


    @Before
    public void setUp() throws Exception
    {
        workers = Executors.newFixedThreadPool( CONCURRENCY );

        Properties props = new Properties();
        props.load( ClassLoader.getSystemResourceAsStream( "test.properties" ) );
        file = new File( props.getProperty( TARGET_PATH_PROP ), "metadata.db" );

        if ( file.exists() )
        {
            FileUtils.forceDelete( file );
        }

        fileDb = new FileDb( file.getAbsolutePath() );
    }


    @After
    public void tearDown() throws Exception
    {
        fileDb.close();

        if ( file.exists() )
        {
            FileUtils.forceDelete( file );
        }
    }


    @Test
    public void testStress() throws Exception
    {
        List<DbWriter> writers = new ArrayList<>( TOTAL );
        for ( int ii = 0; ii < TOTAL; ii++ )
        {
            writers.add( new DbWriter( fileDb ) );
        }

        results = workers.invokeAll( writers );
        workers.shutdown();

        while( ! workers.isTerminated() )
        {
            workers.awaitTermination( 2, TimeUnit.SECONDS );
        }

        long maxTimeAll = 0;
        long maxTimeSuccess = 0;
        long maxTimeFailure = 0;

        long minTimeAll = Long.MAX_VALUE;
        long minTimeSuccess = Long.MAX_VALUE;
        long minTimeFailure = Long.MAX_VALUE;

        long avgTimeAll = 0;
        long avgTimeSuccess = 0;
        long avgTimeFailure = 0;
        int successes = 0;
        int failures = 0;

        for ( Future<Result> future: results )
        {
            Result result = future.get();
            System.out.println( result );

            maxTimeAll = Math.max( maxTimeAll, result.getTime() );
            minTimeAll = Math.min( minTimeAll, result.getTime() );
            avgTimeAll += result.getTime();

            if ( result.isSuccess() )
            {
                successes++;
                minTimeSuccess = Math.min( minTimeSuccess, result.getTime() );
                maxTimeSuccess = Math.max( maxTimeSuccess, result.getTime() );
                avgTimeSuccess += result.getTime();
            }
            else
            {
                failures++;
                minTimeFailure = Math.min( minTimeFailure, result.getTime() );
                maxTimeFailure = Math.max( maxTimeFailure, result.getTime() );
                avgTimeFailure += result.getTime();
            }
        }

        avgTimeAll = avgTimeAll / (successes + failures);

        if ( failures != 0 )
        {
            avgTimeFailure = avgTimeFailure / failures;
        }

        if ( successes != 0 )
        {
            avgTimeSuccess = avgTimeSuccess / successes;
        }

        System.out.println( "------------------------------------------------------------------------" );

        System.out.println( "TOTAL WRITES: " + ( successes + failures ) );
        System.out.println( "SUCCESSFUL WRITES: " + successes );
        System.out.println( "FAILED WRITES: " + failures );
        System.out.println( "CONCURRENCY: " + CONCURRENCY );

        System.out.println( "------------------------------------------------------------------------" );
        System.out.println( "avgTime(ms): " + avgTimeAll );
        System.out.println( "avgTimeSuccess(ms): " + avgTimeSuccess );
        System.out.println( "avgTimeFailure(ms): " + avgTimeFailure );
        System.out.println( "------------------------------------------------------------------------" );

        System.out.println( "minTime(ms): " + minTimeAll );
        System.out.println( "minTimeSuccess(ms): " + minTimeSuccess );
        System.out.println( "minTimeFailure(ms): " + minTimeFailure );
        System.out.println( "------------------------------------------------------------------------" );

        System.out.println( "maxTime(ms): " + minTimeAll );
        System.out.println( "maxTimeSuccess(ms): " + minTimeSuccess );
        System.out.println( "maxTimeFailure(ms): " + minTimeFailure );
        System.out.println( "------------------------------------------------------------------------" );
    }
}
