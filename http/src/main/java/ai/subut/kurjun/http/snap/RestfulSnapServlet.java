package ai.subut.kurjun.http.snap;


import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.http.ServletUtils;


/**
 * This is a wrapper servlet for {@link SnapServlet} and {@link SnapUploadServlet} that has RESTful endpoints.
 *
 */
@Singleton
class RestfulSnapServlet extends HttpServletBase
{

    @Inject
    private SnapServlet snapServlet;

    @Inject
    private SnapUploadServlet uploadServlet;


    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        String pathInfo = req.getPathInfo();
        List<String> pathItems = ServletUtils.splitPath( pathInfo );
        if ( pathItems.isEmpty() )
        {
            badRequest( resp, "Invalid request. Specify either md5 or name/version of package." );
            return;
        }

        if ( pathItems.get( 0 ).equals( SnapServlet.SNAPS_MD5_PARAM ) && pathItems.size() > 1 )
        {
            String md5 = pathItems.get( 1 );
            snapServlet.getByMd5( md5, resp );
        }
        else
        {
            String name = pathItems.get( 0 );
            String version = null;
            if ( pathItems.size() > 1 )
            {
                pathItems.get( 1 );
            }
            snapServlet.getByNameAndVersion( name, version, resp );
        }
    }


    @Override
    protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        uploadServlet.doPost( req, resp );
    }


    @Override
    protected void doDelete( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        List<String> pathItems = ServletUtils.splitPath( req.getPathInfo() );
        if ( pathItems.size() == 1 )
        {
            String md5 = pathItems.get( 0 );
            snapServlet.deletePackage( md5, resp );
        }
        else
        {
            badRequest( resp, "Provide md5 of package to remove" );
        }
    }

}

