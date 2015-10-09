package ai.subut.kurjun.http;


import java.util.EnumSet;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;

import ai.subut.kurjun.cfparser.ControlFileParserModule;
import ai.subut.kurjun.common.KurjunBootstrap;
import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.http.local.KurjunAptRepoServletModule;
import ai.subut.kurjun.http.local.LocalAptRepoServletModule;
import ai.subut.kurjun.http.snap.SnapServletModule;
import ai.subut.kurjun.http.subutai.TemplateServletModule;
import ai.subut.kurjun.index.PackagesIndexParserModule;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreModule;
import ai.subut.kurjun.metadata.storage.file.DbFilePackageMetadataStoreModule;
import ai.subut.kurjun.repo.RepositoryModule;
import ai.subut.kurjun.riparser.ReleaseIndexParserModule;
import ai.subut.kurjun.snap.SnapMetadataParserModule;
import ai.subut.kurjun.storage.factory.FileStoreFactory;
import ai.subut.kurjun.storage.factory.FileStoreModule;
import ai.subut.kurjun.subutai.SubutaiTemplateParserModule;


public class HttpServer
{
    public static final String HTTP_PORT_KEY = "http.port";

    public static final KurjunContext CONTEXT = new KurjunContext( "my" );
    public static final Set<KurjunContext> TEMPLATE_CONTEXTS = new HashSet<>();


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
        bootstrap.addModule( new SubutaiTemplateParserModule() );

        bootstrap.addModule( new SnapMetadataParserModule() );
        bootstrap.addModule( new SnapServletModule().setServletPath( "/snaps" ) );

        bootstrap.addModule( new FileStoreModule() );
        bootstrap.addModule( new PackageMetadataStoreModule() );

        bootstrap.addModule( new RepositoryModule() );

        bootstrap.addModule( new LocalAptRepoServletModule().setServletPath( "/apt" ) );
        bootstrap.addModule( new KurjunAptRepoServletModule().setServletPath( "/vapt" ) );
        bootstrap.addModule( new TemplateServletModule().setServletPath( "/templates" ) );


        bootstrap.boot();

        return bootstrap.getInjector();
    }


    private static void setContexts( KurjunProperties properties )
    {
        Properties p = properties.getContextProperties( CONTEXT );
        p.setProperty( FileStoreFactory.TYPE, FileStoreFactory.FILE_SYSTEM );
        //p.setProperty( FileStoreFactory.TYPE, FileStoreFactory.S3 );

        p.setProperty( PackageMetadataStoreModule.PACKAGE_METADATA_STORE_TYPE, PackageMetadataStoreFactory.FILE_DB );
        // if file db is used, set db file below
        p.setProperty( DbFilePackageMetadataStoreModule.DB_FILE_LOCATION_NAME, "/tmp/kurjun/metadata" );
        // if sql db is used, set connection parameters below:
        p.setProperty( "dataSourceClassName", "org.mariadb.jdbc.MySQLDataSource" );
        p.setProperty( "dataSource.serverName", "localhost" );
        p.setProperty( "dataSource.databaseName", "kurjun_metadata" );
        p.setProperty( "dataSource.user", "kurjun_metadata" );
        p.setProperty( "dataSource.password", "kurjun_metadata" );

        // init template type contexts based on above parameters
        TEMPLATE_CONTEXTS.add( new KurjunContext( "public" ) );
        TEMPLATE_CONTEXTS.add( new KurjunContext( "trust" ) );
        for ( KurjunContext kc : TEMPLATE_CONTEXTS )
        {
            Properties kcp = properties.getContextProperties( kc );
            kcp.putAll( p );

            // set custom meta data location
            String s = p.getProperty( DbFilePackageMetadataStoreModule.DB_FILE_LOCATION_NAME ) + "/templates/" + kc.getName();
            kcp.setProperty( DbFilePackageMetadataStoreModule.DB_FILE_LOCATION_NAME, s );

        }

    }
}

