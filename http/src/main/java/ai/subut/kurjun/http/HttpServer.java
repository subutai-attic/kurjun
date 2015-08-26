package ai.subut.kurjun.http;


import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;

import ai.subut.kurjun.cfparser.ControlFileParserModule;
import ai.subut.kurjun.common.KurjunBootstrap;
import ai.subut.kurjun.common.KurjunProperties;
import ai.subut.kurjun.http.local.KurjunAptRepoServletModule;
import ai.subut.kurjun.http.local.LocalAptRepoServletModule;
import ai.subut.kurjun.http.snap.SnapServletModule;
import ai.subut.kurjun.index.PackagesIndexParserModule;
import ai.subut.kurjun.metadata.storage.file.DbFilePackageMetadataStoreModule;
import ai.subut.kurjun.repo.RepositoryModule;
import ai.subut.kurjun.riparser.ReleaseIndexParserModule;
import ai.subut.kurjun.snap.SnapMetadataParserModule;
import ai.subut.kurjun.snap.metadata.store.SnapMetadataStoreModule;
import ai.subut.kurjun.storage.fs.FileSystemFileStoreModule;


public class HttpServer
{
    public static final String HTTP_PORT_KEY = "http.port";


    public static void main( String[] args ) throws Exception
    {

        Injector injector = bootstrapDI();
        KurjunProperties properties = injector.getInstance( KurjunProperties.class );

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
        bootstrap.addModule( new SnapMetadataStoreModule() );
        bootstrap.addModule( new SnapServletModule().setServletPath( "/snaps" ) );

        bootstrap.addModule( new FileSystemFileStoreModule() );
        bootstrap.addModule( new DbFilePackageMetadataStoreModule() );

        bootstrap.addModule( new RepositoryModule() );

        bootstrap.addModule( new LocalAptRepoServletModule().setServletPath( "/apt" ) );
        bootstrap.addModule( new KurjunAptRepoServletModule().setServletPath( "/vapt" ) );


        bootstrap.boot();

        return bootstrap.getInjector();
    }
}

