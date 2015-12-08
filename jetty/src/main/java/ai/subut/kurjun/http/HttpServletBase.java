package ai.subut.kurjun.http;


import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.security.Permission;
import ai.subut.kurjun.security.service.AuthManager;


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


    public boolean checkAuthentication( HttpServletRequest req, Permission permission )
    {
        // this is to avoid using pgp key fingerprint in dev mode
        boolean dev = true;
        if ( dev )
        {
            return true;
        }

        // try to get fingerprint from header
        String fingerprint = req.getHeader( KurjunConstants.HTTP_HEADER_FINGERPRINT );

        // if not set in header, check params
        if ( fingerprint == null )
        {
            fingerprint = req.getParameter( KurjunConstants.HTTP_PARAM_FINGERPRINT );
        }

        if ( fingerprint != null )
        {
            return getAuthManager().isAllowed( fingerprint, permission, getContext() );
        }
        return false;
    }


    protected abstract KurjunContext getContext();


    protected abstract AuthManager getAuthManager();


    /**
     * Gets decoded MD5 checksum value from request parameters.
     *
     * @param req http request from which parameter value is extracted
     * @return MD5 checksum if valid value is found; {@code null} otherwise
     */
    protected byte[] getMd5ParameterValue( HttpServletRequest req )
    {
        String md5 = req.getParameter( MD5_PARAM );
        if ( md5 != null )
        {
            try
            {
                return Hex.decodeHex( md5.toCharArray() );
            }
            catch ( DecoderException ex )
            {
            }
        }
        return null;
    }


    protected void ok( HttpServletResponse resp, String msg ) throws IOException
    {
        writeResponse( resp, HttpServletResponse.SC_OK, msg );
    }


    protected void badRequest( HttpServletResponse resp, String msg ) throws IOException
    {
        writeResponse( resp, HttpServletResponse.SC_BAD_REQUEST, msg );
    }


    protected void forbidden( HttpServletResponse resp, String msg ) throws IOException
    {
        writeResponse( resp, HttpServletResponse.SC_FORBIDDEN, msg );
    }


    protected void forbidden( HttpServletResponse resp ) throws IOException
    {
        writeResponse( resp, HttpServletResponse.SC_FORBIDDEN, "No permissions." );
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

