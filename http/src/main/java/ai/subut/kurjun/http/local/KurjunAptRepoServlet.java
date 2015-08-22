package ai.subut.kurjun.http.local;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.index.service.PackagesIndexBuilder;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.repo.util.ReleaseIndexBuilder;


/**
 * Servlet to serve repository metadata like release and package index files, keys, etc of a virtual apt repository.
 */
@Singleton
class KurjunAptRepoServlet extends HttpServlet
{

    private LocalRepository repository;

    @Inject
    private PackagesIndexBuilder packagesIndexBuilder;

    @Inject
    private ReleaseIndexBuilder releaseIndexBuilder;


    @Inject
    public KurjunAptRepoServlet( LocalRepository repository )
    {
        this.repository = repository;
    }


    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        resp.setContentType( "text/plain" );
        resp.setCharacterEncoding( StandardCharsets.UTF_8.name() );

        AptUrlPathParser info = new AptUrlPathParser( req.getPathInfo() );
        if ( info.isPackagesIndexFile() )
        {
            generatePackagesIndex( info.getRelease(), info.getComponent(), info.getArchitecture(),
                                   info.getCompressionType(), resp );
        }
        else if ( info.isReleaseIndexFile() )
        {
            generateReleaseIndexFile( info.getRelease(), resp );
        }
        else
        {
            writeResponse( resp, HttpServletResponse.SC_NOT_FOUND, "Specified path does not exist or is not a file" );
        }
    }


    private void generatePackagesIndex( String release, String component, Architecture arch,
                                        CompressionType compressionType, HttpServletResponse resp ) throws IOException
    {
        Optional<ReleaseFile> distr = repository.getDistributions().stream()
                .filter( r -> r.getCodename().equals( release ) ).findFirst();
        if ( !distr.isPresent() )
        {
            writeResponse( resp, HttpServletResponse.SC_NOT_FOUND, "Release not found" );
            return;
        }
        if ( distr.get().getComponent( component ) == null )
        {
            writeResponse( resp, HttpServletResponse.SC_NOT_FOUND, "Component not found" );
            return;
        }
        if ( arch == null )
        {
            writeResponse( resp, HttpServletResponse.SC_NOT_FOUND, "Architecture not supported" );
            return;
        }

        try ( OutputStream os = resp.getOutputStream() )
        {
            packagesIndexBuilder.buildIndex( component, arch, os, compressionType );
        }
    }


    private void generateReleaseIndexFile( String release, HttpServletResponse resp ) throws IOException
    {
        Optional<ReleaseFile> item = repository.getDistributions().stream()
                .filter( r -> r.getCodename().equals( release ) ).findFirst();
        if ( item.isPresent() )
        {
            String releaseIndex = releaseIndexBuilder.build( item.get(), repository.isKurjun() );
            writeResponse( resp, HttpServletResponse.SC_OK, releaseIndex );
        }
        else
        {
            writeResponse( resp, HttpServletResponse.SC_NOT_FOUND, "Release not found" );
        }
    }


    private void writeResponse( HttpServletResponse resp, int statusCode, String msg ) throws IOException
    {
        resp.setStatus( statusCode );
        try ( ServletOutputStream os = resp.getOutputStream() )
        {
            os.print( msg );
        }
    }

}

