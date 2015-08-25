package ai.subut.kurjun.http;


import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Properties;

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

import ai.subut.kurjun.cfparser.ControlFileParserModule;
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
    public static final String ROOT_LOCATION_KEY = "snaps.store.location";


    public static void main( String[] args ) throws Exception
    {

        Properties properties = readProperties();

        Injector injector = bootstrapDI( properties );

        FilterHolder f = new FilterHolder( injector.getInstance( GuiceFilter.class ) );

        ServletContextHandler handler = new ServletContextHandler( ServletContextHandler.SESSIONS );
        handler.setContextPath( "/" );
        handler.addFilter( f, "/*", EnumSet.allOf( DispatcherType.class ) );

        Server server = new Server( 8080 );
        server.setHandler( handler );

        server.start();
        server.join();
    }


    private static Properties readProperties() throws IOException
    {
        Properties properties = new Properties();
        Path path = Paths.get( "app.properties" );
        if ( Files.exists( path ) )
        {
            try ( Reader reader = new FileReader( path.toFile() ) )
            {
                properties.load( reader );
            }
        }
        else
        {
            try ( Reader reader = new InputStreamReader( ClassLoader.getSystemResourceAsStream( path.toString() ) ) )
            {
                properties.load( reader );
            }
        }
        return properties;
    }


    /**
     * Starts and configures Guice DI.
     */
    private static Injector bootstrapDI( Properties properties )
    {

        Collection<Module> modules = new ArrayList<>();
        modules.add( new ControlFileParserModule() );
        modules.add( new ReleaseIndexParserModule() );
        modules.add( new PackagesIndexParserModule() );

        String rootDir = properties.getProperty( ROOT_LOCATION_KEY );

        modules.add( new SnapMetadataParserModule() );
        modules.add( new SnapMetadataStoreModule( Paths.get( rootDir, "metadata" ).toString() ) );
        modules.add( new SnapServletModule().setServletPath( "/snap" ) );

        modules.add( new FileSystemFileStoreModule().setRootLocation( Paths.get( rootDir, "files" ).toString() ) );
        modules.add( new DbFilePackageMetadataStoreModule() );

        modules.add( new RepositoryModule() );

        modules.add( new LocalAptRepoServletModule().setServletPath( "/apt" ) );
        modules.add( new KurjunAptRepoServletModule().setServletPath( "/vapt" ) );

        // setup necessary instance bindings here
        modules.add( new AbstractModule()
        {
            @Override
            protected void configure()
            {
                bind( String.class )
                        .annotatedWith( Names.named( DbFilePackageMetadataStoreModule.DB_FILE_LOCATION_NAME ) )
                        .toInstance( rootDir );
            }
        } );

        return Guice.createInjector( modules );

    }
}

