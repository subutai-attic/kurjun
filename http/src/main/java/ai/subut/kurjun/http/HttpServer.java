package ai.subut.kurjun.http;


import java.util.EnumSet;
import java.util.Properties;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.datastax.driver.core.Session;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;

import ai.subut.kurjun.cfparser.ControlFileParserModule;
import ai.subut.kurjun.common.KurjunBootstrap;
import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.http.local.KurjunAptRepoServletModule;
import ai.subut.kurjun.http.local.LocalAptRepoServletModule;
import ai.subut.kurjun.http.snap.SnapServletModule;
import ai.subut.kurjun.index.PackagesIndexParserModule;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreModule;
import ai.subut.kurjun.metadata.storage.file.DbFilePackageMetadataStoreModule;
import ai.subut.kurjun.metadata.storage.nosql.CassandraConnector;
import ai.subut.kurjun.repo.RepositoryModule;
import ai.subut.kurjun.riparser.ReleaseIndexParserModule;
import ai.subut.kurjun.snap.SnapMetadataParserModule;
import ai.subut.kurjun.storage.factory.FileStoreFactory;
import ai.subut.kurjun.storage.factory.FileStoreModule;
import ai.subut.kurjun.storage.fs.FileSystemFileStoreModule;


public class HttpServer
{
    public static final String HTTP_PORT_KEY = "http.port";

    public static final KurjunContext CONTEXT = new KurjunContext( "my" );


    public static void main( String[] args ) throws Exception
    {

        Injector injector = bootstrapDI();
        KurjunProperties properties = injector.getInstance( KurjunProperties.class );
        setContexts( properties );

        FilterHolder f = new FilterHolder( injector.getInstance( GuiceFilter.class ) );

        ServletContextHandler handler = new ServletContextHandler( ServletContextHandler.SESSIONS );
        handler.setContextPath( "/" );
        handler.addFilter( f, "/*", EnumSet.allOf( DispatcherType.class ) );

        Server server = new Server( properties.getIntegerWithDefault( HTTP_PORT_KEY, 8080 ) );
        server.setHandler( handler );

        server.start();
        server.join();
    }


    /**
     * Starts and configures Guice DI.
     */
    private static Injector bootstrapDI()
    {

        KurjunBootstrap bootstrap = new KurjunBootstrap();
        bootstrap.addModule( new ControlFileParserModule() );
        bootstrap.addModule( new ReleaseIndexParserModule() );
        bootstrap.addModule( new PackagesIndexParserModule() );

        bootstrap.addModule( new SnapMetadataParserModule() );
        bootstrap.addModule( new SnapServletModule().setServletPath( "/snaps" ) );

        bootstrap.addModule( new AbstractModule()
        {
            @Override
            protected void configure()
            {
                bind( Session.class ).toProvider( CassandraConnector.getInstance() );
            }
        } );
        bootstrap.addModule( new FileStoreModule() );
        bootstrap.addModule( new PackageMetadataStoreModule() );

        bootstrap.addModule( new RepositoryModule() );

        bootstrap.addModule( new LocalAptRepoServletModule().setServletPath( "/apt" ) );
        bootstrap.addModule( new KurjunAptRepoServletModule().setServletPath( "/vapt" ) );


        bootstrap.boot();

        return bootstrap.getInjector();
    }


    private static void setContexts( KurjunProperties properties )
    {
        Properties p = properties.getContextProperties( CONTEXT );
        p.setProperty( FileStoreFactory.TYPE, FileStoreFactory.FILE_SYSTEM );
        // --- begin S3 file store  ---
        //p.setProperty( FileStoreFactory.TYPE, FileStoreFactory.S3 );
        //p.setProperty( S3FileStoreModule.BUCKET_NAME, "kurjun-test" );
        // --- end S3 file store    ---

        p.setProperty( FileSystemFileStoreModule.ROOT_DIRECTORY, "/tmp/kurjun/files" );

        p.setProperty( PackageMetadataStoreModule.PACKAGE_METADATA_STORE_TYPE, PackageMetadataStoreFactory.FILE_DB );
        // if file db is used, set db file below
        p.setProperty( DbFilePackageMetadataStoreModule.DB_FILE_LOCATION_NAME, "/tmp/kurjun/metadata" );

    }
}

