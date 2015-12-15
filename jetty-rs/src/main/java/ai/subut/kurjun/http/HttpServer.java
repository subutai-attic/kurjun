package ai.subut.kurjun.http;


import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.subut.kurjun.rest.template.RestTemplateManagerImpl;
import ai.subut.kurjun.rest.vapt.RestAptManagerImpl;
import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


public class HttpServer
{
    private static final Logger LOGGER = LoggerFactory.getLogger( HttpServer.class );


    public static void main( String[] args )
    {
        try
        {
            Server server = new Server( 8081 );
            // Configuring all static web resource
            // final ServletHolder staticHolder = new ServletHolder(new DefaultServlet());

            // Register and map the dispatcher servlet
            final ServletHolder servletHolder = new ServletHolder( new CXFNonSpringJaxrsServlet() );
            final ServletContextHandler context = new ServletContextHandler( ServletContextHandler.SESSIONS );
            context.setContextPath( "/" );
            context.addServlet( servletHolder, "/rest/kurjun/*" );
            servletHolder.setInitParameter( "jaxrs.serviceClasses",
                    RestAptManagerImpl.class.getName() + ","
                    + RestTemplateManagerImpl.class.getName() );

            server.setHandler( context );

            ServerConnector http = new ServerConnector( server );
            http.setHost( "0.0.0.0" );
//            http.setHost( "localhost" );
            http.setIdleTimeout( 30000 );

            server.addConnector( http );

            server.start();
            LOGGER.info( "Kurjun Jetty Server Started." );
            server.join();
        }
        catch ( Exception ex )
        {
            LOGGER.error( "", ex );
            System.exit( -1 );
        }
        finally
        {
            LOGGER.info( "Done!" );
        }
    }

}
