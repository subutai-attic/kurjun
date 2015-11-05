package ai.subut.kurjun.http.local;


import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.http.HttpServer;
import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.http.ServletUtils;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.repo.RepositoryFactory;


@Singleton
@MultipartConfig
class KurjunAptRepoUploadServlet extends HttpServletBase
{


    @Inject
    private RepositoryFactory repositoryFactory;

    private LocalRepository repository;


    @Override
    public void init() throws ServletException
    {
        KurjunContext context = HttpServer.CONTEXT;
        repository = repositoryFactory.createLocalApt( context );
    }


    @Override
    protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        if ( !ServletUtils.isMultipart( req ) )
        {
            badRequest( resp, "Not a multipart request" );
            return;
        }

        ServletUtils.setMultipartConfig( req, this.getClass() );

        Part part = req.getPart( PACKAGE_FILE_PART_NAME );
        if ( part != null )
        {
            try ( InputStream is = part.getInputStream() )
            {
                repository.put( is );
            }
        }
        else
        {
            String msg = String.format( "No package attached with name '%s'", PACKAGE_FILE_PART_NAME );
            badRequest( resp, msg );
        }
    }

}

