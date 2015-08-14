package ai.subut.kurjun.http.local;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.model.repository.LocalRepository;


@Singleton
class LocalAptRepoServlet extends HttpServlet
{

    private LocalRepository repository;


    @Inject
    public LocalAptRepoServlet( LocalRepository repository )
    {
        this.repository = repository;
        // TODO: injected repo shall be already inited
        this.repository.init( "/var/www/repos/apt/hub" );
    }


    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        resp.setContentType( "text/plain" );
        resp.setCharacterEncoding( StandardCharsets.UTF_8.name() );

        String pathWithoutLeadingSlash = req.getPathInfo().substring( 1 );

        try ( ServletOutputStream out = resp.getOutputStream() )
        {
            Path path = repository.getBaseDirectoryPath().resolve( pathWithoutLeadingSlash );
            if ( Files.exists( path ) )
            {
                Files.copy( path, out );
            }
            else
            {
                resp.setStatus( HttpServletResponse.SC_NOT_FOUND );
                out.print( "Specified path does not exist or is not a file" );
            }
        }
    }


}

