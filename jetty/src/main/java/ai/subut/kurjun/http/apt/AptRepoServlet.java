package ai.subut.kurjun.http.apt;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.http.HttpServer;
import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.security.Permission;
import ai.subut.kurjun.repo.util.PackagesProviderFactory;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.repo.service.PackagesIndexBuilder;
import ai.subut.kurjun.repo.util.AptIndexBuilderFactory;
import ai.subut.kurjun.repo.util.ReleaseIndexBuilder;
import ai.subut.kurjun.security.service.AuthManager;


/**
 * Servlet to serve repository metadata like release and package index files, keys, etc of a virtual apt repository.
 */
@Singleton
class AptRepoServlet extends HttpServletBase
{

    @Inject
    private AuthManager authManager;

    @Inject
    private AptIndexBuilderFactory indexBuilderFactory;

    @Inject
    private PackagesProviderFactory packagesProviderFactory;

    @Inject
    private RepositoryFactory repositoryFactory;

    private KurjunContext context;


    @Override
    public void init() throws ServletException
    {
        context = HttpServer.CONTEXT;
    }


    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        if ( !checkAuthentication( req, Permission.GET_PACKAGE ) )
        {
            forbidden( resp );
            return;
        }

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
            notFound( resp, "Specified path does not exist or is not a file" );
        }
    }


    @Override
    protected KurjunContext getContext()
    {
        return context;
    }


    @Override
    protected AuthManager getAuthManager()
    {
        return authManager;
    }


    private LocalRepository getRepository()
    {
        return repositoryFactory.createLocalApt( context );
    }


    private void generatePackagesIndex( String release, String component, Architecture arch,
                                        CompressionType compressionType, HttpServletResponse resp ) throws IOException
    {
        LocalRepository repository = getRepository();
        Optional<ReleaseFile> distr = repository.getDistributions().stream()
                .filter( r -> r.getCodename().equals( release ) ).findFirst();
        if ( !distr.isPresent() )
        {
            notFound( resp, "Release not found" );
            return;
        }
        if ( distr.get().getComponent( component ) == null )
        {
            notFound( resp, "Component not found" );
            return;
        }
        if ( arch == null )
        {
            notFound( resp, "Architecture not supported" );
            return;
        }

        if ( compressionType != CompressionType.NONE )
        {
            // make archived package indices downloadable
            String filename = "Packages." + compressionType.getExtension();
            resp.setHeader( "Content-Disposition", " attachment; filename=" + filename );
        }

        PackagesIndexBuilder packagesIndexBuilder = indexBuilderFactory.createPackagesIndexBuilder( context );
        try ( OutputStream os = resp.getOutputStream() )
        {
            packagesIndexBuilder.buildIndex( packagesProviderFactory.create( repository, component, arch ), os,
                                             compressionType );
        }
    }


    private void generateReleaseIndexFile( String release, HttpServletResponse resp ) throws IOException
    {
        LocalRepository repository = getRepository();
        Optional<ReleaseFile> item = repository.getDistributions().stream()
                .filter( r -> r.getCodename().equals( release ) ).findFirst();
        if ( item.isPresent() )
        {
            ReleaseIndexBuilder releaseIndexBuilder = indexBuilderFactory.createReleaseIndexBuilder( context );
            String releaseIndex = releaseIndexBuilder.build( item.get(), repository.isKurjun() );
            ok( resp, releaseIndex );
        }
        else
        {
            notFound( resp, "Release not found" );
        }
    }


}

