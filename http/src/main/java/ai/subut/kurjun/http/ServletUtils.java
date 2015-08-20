package ai.subut.kurjun.http;


import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.Request;


/**
 * Utility methods to work with servlets.
 *
 */
public class ServletUtils
{

    private ServletUtils()
    {
        // no need to construct utility class
    }


    /**
     * Prepends a slash to the beginning of the given path.
     *
     * @param path
     * @return
     */
    public static String ensureLeadingSlash( String path )
    {
        if ( path.startsWith( "/" ) )
        {
            return path;
        }
        return "/" + path;
    }


    /**
     * Removes that trailing slash from the given path.
     *
     * @param path
     * @return
     */
    public static String removeTrailingSlash( String path )
    {
        if ( path.endsWith( "/" ) )
        {
            return path.substring( 0, path.length() - 1 );
        }
        return path;
    }


    /**
     * Splits the supplied path into parts. If supplied path can not be split, an empty list is returned.
     *
     * @param path path to split into parts
     * @return list of path parts
     */
    public static List<String> splitPath( String path )
    {
        if ( path == null )
        {
            return Collections.emptyList();
        }

        List<String> ls = new ArrayList<>();

        String[] arr = path.split( "/" );
        for ( String s : arr )
        {
            int index;
            if ( ( index = s.indexOf( "?" ) ) > -1 )
            {
                if ( index > 0 )
                {
                    ls.add( s.substring( 0, index ) );
                }
            }
            else if ( ( index = s.indexOf( "#" ) ) > -1 )
            {
                if ( index > 0 )
                {
                    ls.add( s.substring( 0, index ) );
                }
            }
            else if ( !s.isEmpty() )
            {
                ls.add( s );
            }
        }
        return ls;
    }


    /**
     * Checks if supplied servlet request is a multipart form data.
     *
     * @param req servlet request to check
     * @return {@code true} if request can definitely be identified as a multipart form data; {@code false} otherwise
     */
    public static boolean isMultipart( ServletRequest req )
    {
        return req.getContentType() != null && req.getContentType().toLowerCase().contains( "multipart/form-data" );
    }


    /**
     * Enables correct handling of multipart requests by servlets of supplied type. This is a workaround to setup
     * multipart config when servlets are bound by Guice servlet module.
     * <p>
     * This workaround was found here https://github.com/google/guice/issues/898
     *
     * @param req a multipart request to be handled
     * @param servletClass servlet type that will handle the request
     */
    public static void setMultipartConfig( HttpServletRequest req, Class servletClass )
    {
        MultipartConfigElement mc;

        Annotation annotation = servletClass.getAnnotation( MultipartConfig.class );
        if ( annotation instanceof MultipartConfig )
        {
            mc = new MultipartConfigElement( ( MultipartConfig ) annotation );
        }
        else
        {
            mc = new MultipartConfigElement( "" );
        }
        req.setAttribute( Request.__MULTIPART_CONFIG_ELEMENT, mc );
    }

}

