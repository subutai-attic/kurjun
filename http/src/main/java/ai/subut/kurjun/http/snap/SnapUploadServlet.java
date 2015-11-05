package ai.subut.kurjun.http.snap;


import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.http.HttpServer;
import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.http.ServletUtils;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.repo.RepositoryFactory;


@Singleton
@MultipartConfig
class SnapUploadServlet extends HttpServletBase
{

    @Inject
    private RepositoryFactory repositoryFactory;

    private KurjunContext context;


    @Override
    public void init() throws ServletException
    {
        this.context = HttpServer.CONTEXT;
    }


    @Override
    protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        if ( ServletUtils.isMultipart( req ) )
        {
            ServletUtils.setMultipartConfig( req, this.getClass() );

            Part part = req.getPart( PACKAGE_FILE_PART_NAME );
            if ( part != null && part.getSubmittedFileName() != null )
            {
                parsePackageFile( part, resp );
            }
            else
            {
                String msg = String.format( "No package file attached with name '%s'", PACKAGE_FILE_PART_NAME );
                badRequest( resp, msg );
            }
        }
        else
        {
            badRequest( resp, "Request is not a multipart request" );
        }
    }


    private void parsePackageFile( Part part, HttpServletResponse resp ) throws IOException
    {
        // define file compression type based on submitted file name
        String fileName = part.getSubmittedFileName();
        CompressionType compressionType = CompressionType.getCompressionType( fileName );

        try ( InputStream is = part.getInputStream() )
        {
            LocalRepository repo = getRepository();
            repo.put( is, compressionType );
            ok( resp, "Package successfully saved" );
        }
        catch ( IOException ex )
        {
            internalServerError( resp, "Failed to upload package: " + ex.getMessage() );
        }
    }


    private LocalRepository getRepository()
    {
        return repositoryFactory.createLocalSnap( context );
    }

}

