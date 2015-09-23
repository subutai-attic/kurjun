package ai.subut.kurjun.http;


import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;


/**
 * Abstract {@link HttpServlet} class with helper methods.
 *
 */
public abstract class HttpServletBase extends HttpServlet
{

    public static final String MD5_PARAM = "md5";
    public static final String NAME_PARAM = "name";
    public static final String VERSION_PARAM = "version";
    public static final String PACKAGE_FILE_PART_NAME = "package";


    protected void ok( HttpServletResponse resp, String msg ) throws IOException
    {
        writeResponse( resp, HttpServletResponse.SC_OK, msg );
    }


    protected void badRequest( HttpServletResponse resp, String msg ) throws IOException
    {
        writeResponse( resp, HttpServletResponse.SC_BAD_REQUEST, msg );
    }


    protected void notFound( HttpServletResponse resp, String msg ) throws IOException
    {
        writeResponse( resp, HttpServletResponse.SC_NOT_FOUND, msg );
    }


    protected void internalServerError( HttpServletResponse resp, String msg ) throws IOException
    {
        writeResponse( resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg );
    }


    protected void writeResponse( HttpServletResponse resp, int statusCode, String msg ) throws IOException
    {
        resp.setStatus( statusCode );
        try ( ServletOutputStream os = resp.getOutputStream() )
        {
            os.print( msg );
        }
    }

}

