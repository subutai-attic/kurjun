package ai.subut.kurjun.http;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;

import ai.subut.kurjun.rest.raw.RestRawManagerImpl;
import ai.subut.kurjun.rest.template.RestTemplateManagerImpl;
import ai.subut.kurjun.rest.vapt.RestAptManagerImpl;


public class HttpServer
{
    private static final Logger LOGGER = LoggerFactory.getLogger( HttpServer.class );


    public static void main( String[] args )
    {
        try
        {
            Server server = new Server( 8081 );

            final ServletHolder servletHolder = new ServletHolder( new CXFNonSpringJaxrsServlet() );
            final ServletContextHandler context = new ServletContextHandler( ServletContextHandler.SESSIONS );

            context.setContextPath( "/" );
            context.addServlet( servletHolder, "/rest/kurjun/*" );

            servletHolder.setInitParameter( "jaxrs.serviceClasses",
                    RestAptManagerImpl.class.getName() + ","
                    + RestRawManagerImpl.class.getName() + ","
                    + RestTemplateManagerImpl.class.getName() );

            server.setHandler( context );
            ServerConnector http = new ServerConnector( server );

            http.setHost( "0.0.0.0" );
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
