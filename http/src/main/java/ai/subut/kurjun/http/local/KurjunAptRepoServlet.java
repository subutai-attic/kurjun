package ai.subut.kurjun.http.local;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.model.index.ChecksummedResource;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.repo.http.PathBuilder;


/**
 * Servlet to serve repository metadata like release and package index files, keys, etc of a virtual apt repository.
 */
@Singleton
class KurjunAptRepoServlet extends HttpServlet
{

    private LocalRepository repository;
    private Set<String> indexFilePaths = new HashSet<>();


    @Inject
    public KurjunAptRepoServlet( LocalRepository repository )
    {
        this.repository = repository;
        // TODO: injected repo shall be already inited
        this.repository.init( "/var/www/repos/apt/hub" );

        buildPaths();
    }


    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        resp.setContentType( "text/plain" );
        resp.setCharacterEncoding( StandardCharsets.UTF_8.name() );

        String pathWithoutLeadingSlash = req.getPathInfo().substring( 1 );

        try ( ServletOutputStream out = resp.getOutputStream() )
        {
            if ( indexFilePaths.contains( pathWithoutLeadingSlash ) )
            {
                // TODO: generate index file and stream it to response
            }
            else
            {
                resp.setStatus( HttpServletResponse.SC_NOT_FOUND );
                out.print( "Specified path does not exist or is not a file" );
            }
        }
    }


    private void buildPaths()
    {
        Set<ReleaseFile> releases = repository.getDistributions();
        for ( ReleaseFile release : releases )
        {
            String path = PathBuilder.instance().forReleaseIndexFile().setRelease( release ).build();
            indexFilePaths.add( path );

            List<ChecksummedResource> indices = release.getIndices();
            for ( ChecksummedResource indexFile : indices )
            {
                path = PathBuilder.instance().setRelease( release ).setResource( indexFile ).build();
                indexFilePaths.add( path );
            }
        }
    }


}

