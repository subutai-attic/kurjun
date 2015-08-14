package ai.subut.kurjun.http;


import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceFilter;

import ai.subut.kurjun.http.local.LocalAptRepoServletModule;
import ai.subut.kurjun.index.PackagesIndexParserModule;
import ai.subut.kurjun.metadata.storage.file.DbFilePackageMetadataStoreModule;
import ai.subut.kurjun.repo.RepositoryModule;
import ai.subut.kurjun.riparser.ReleaseIndexParserModule;
import ai.subut.kurjun.storage.fs.FileSystemFileStoreModule;


public class HttpServer
{
    public static void main( String[] args ) throws Exception
    {

        Injector injector = bootstrapDI();

        FilterHolder f = new FilterHolder( injector.getInstance( GuiceFilter.class ) );

        ServletContextHandler handler = new ServletContextHandler( ServletContextHandler.SESSIONS );
        handler.setContextPath( "/" );
        handler.addFilter( f, "/*", EnumSet.allOf( DispatcherType.class ) );

        Server server = new Server( 8080 );
        server.setHandler( handler );

        server.start();
        server.join();
    }


    /**
     * Starts and configures Guice DI.
     */
    private static Injector bootstrapDI()
    {

        Collection<Module> modules = new ArrayList<>();
        modules.add( new ReleaseIndexParserModule() );
        modules.add( new PackagesIndexParserModule() );
        modules.add( new RepositoryModule() );
        modules.add( new FileSystemFileStoreModule() );
        modules.add( new DbFilePackageMetadataStoreModule() );

        LocalAptRepoServletModule servletModule = new LocalAptRepoServletModule();
        servletModule.setServletPath( "/apt" );
        modules.add( servletModule );

        // setup necessary instance bindings here
        modules.add( new AbstractModule()
        {
            @Override
            protected void configure()
            {
                bind( String.class )
                        .annotatedWith( Names.named( DbFilePackageMetadataStoreModule.DB_FILE_LOCATION_NAME ) )
                        .toInstance( "/home/azilet/tmp/kurjun/metadata" );
            }
        } );

        return Guice.createInjector( modules );

    }
}

