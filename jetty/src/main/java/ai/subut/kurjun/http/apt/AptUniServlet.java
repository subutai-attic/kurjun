package ai.subut.kurjun.http.apt;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.http.HttpServer;
import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.http.ServletUtils;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;

import ai.subut.kurjun.model.repository.Repository;
import ai.subut.kurjun.model.repository.UnifiedRepository;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.repo.service.PackageFilenameBuilder;
import ai.subut.kurjun.repo.service.PackageFilenameParser;
import ai.subut.kurjun.repo.service.PackagesIndexBuilder;
import ai.subut.kurjun.repo.util.AptIndexBuilderFactory;
import ai.subut.kurjun.repo.util.PackagesProviderFactory;
import ai.subut.kurjun.repo.util.ReleaseIndexBuilder;
import ai.subut.kurjun.security.service.AuthManager;


/**
 * This servlet demonstrates unified apt repository features. For testing and demonstration purposes only.
 *
 */
@Singleton
public class AptUniServlet extends HttpServletBase
{

    @Inject
    private RepositoryFactory repositoryFactory;

    @Inject
    private AptIndexBuilderFactory indexBuilderFactory;

    @Inject
    private PackagesProviderFactory packagesProviderFactory;

    @Inject
    private PackageFilenameParser filenameParser;

    @Inject
    private PackageFilenameBuilder filenameBuilder;

    @Inject
    private Gson gson;


    private KurjunContext context;


    @Override
    public void init() throws ServletException
    {
        this.context = HttpServer.CONTEXT;
    }


    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        String pathInfo = req.getPathInfo();
        List<String> pathItems = ServletUtils.splitPath( pathInfo );
        if ( pathItems.isEmpty() )
        {
            badRequest( resp, "Invalid path." );
            return;
        }

        UnifiedRepository uni = getRepository();

        if ( pathItems.get( 0 ).equals( "pool" ) )
        {
            handlePoolRequest( req, resp );
            return;
        }

        // to test metadata caching
        if ( pathItems.get( 0 ).equals( "list" ) )
        {
            List<SerializableMetadata> all = new LinkedList<>();
            for ( Repository repo : uni.getRepositories() )
            {
                if ( repo instanceof NonLocalRepository )
                {
                    NonLocalRepository remote = ( NonLocalRepository ) repo;
                    List<SerializableMetadata> ls = remote.getMetadataCache().getMetadataList();
                    all.addAll( ls );
                }
                else
                {
                    all.addAll( repo.listPackages() );
                }
            }
            resp.setContentType( "application/json" );
            try ( Writer w = resp.getWriter() )
            {
                for ( SerializableMetadata item : all )
                {
                    w.append( item.serialize() ).append( System.lineSeparator() );
                }
            }
            return;
        }

        resp.setContentType( "text/plain" );

        AptUrlPathParser urlParser = new AptUrlPathParser( pathInfo );
        if ( urlParser.isPackagesIndexFile() )
        {
            PackagesIndexBuilder packagesIndexBuilder = indexBuilderFactory.createPackagesIndexBuilder( context );
            try ( OutputStream os = resp.getOutputStream() )
            {
                PackagesIndexBuilder.PackagesProvider packages
                        = packagesProviderFactory.create( uni, "main", urlParser.getArchitecture() );
                packagesIndexBuilder.buildIndex( packages, os, urlParser.getCompressionType() );
            }
        }
        else if ( urlParser.isReleaseIndexFile() )
        {
            ReleaseFile release = getReleaseByName( urlParser.getRelease(), uni );
            if ( release != null )
            {
                ReleaseIndexBuilder releaseIndexBuilder = indexBuilderFactory.createReleaseIndexBuilder( uni, context );
                String releaseIndex = releaseIndexBuilder.build( release, uni.isKurjun() );
                ok( resp, releaseIndex );
            }
            else
            {
                internalServerError( resp, "Release not found: " + urlParser.getRelease() );
            }
        }
        else
        {
            notFound( resp, "Specified path does not exists or not supported." );
        }
    }


    @Override
    protected KurjunContext getContext()
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }


    @Override
    protected AuthManager getAuthManager()
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }


    private void handlePoolRequest( HttpServletRequest req, HttpServletResponse resp ) throws IOException
    {
        String name = filenameParser.getPackageFromFilename( req.getPathInfo() );
        String version = filenameParser.getVersionFromFilename( req.getPathInfo() );
        if ( name == null || version == null )
        {
            badRequest( resp, "Invalid pool path" );
            return;
        }

        DefaultMetadata m = new DefaultMetadata();
        m.setName( name );
        m.setVersion( version );

        UnifiedRepository repo = getRepository();
        SerializableMetadata metadata = repo.getPackageInfo( m );

        if ( metadata != null )
        {
            try ( InputStream is = repo.getPackageStream( metadata ) )
            {
                if ( is != null )
                {
                    DefaultPackageMetadata pm = gson.fromJson( metadata.serialize(), DefaultPackageMetadata.class );
                    String filename = filenameBuilder.makePackageFilename( pm );
                    resp.setHeader( "Content-Disposition", "attachment; filename=" + filename );
                    IOUtils.copy( is, resp.getOutputStream() );
                }
                else
                {
                    notFound( resp, "Package file not found" );
                }
            }
        }
        else
        {
            notFound( resp, "Package not found" );
        }
    }


    private UnifiedRepository getRepository() throws MalformedURLException
    {
        NonLocalRepository remote = repositoryFactory.createNonLocalApt( new URL( "http://10.0.3.156:8080/vapt" ) );
        LocalRepository local = repositoryFactory.createLocalApt( context );

        UnifiedRepository uni = repositoryFactory.createUnifiedRepo();
        uni.getRepositories().add( remote );
        uni.getRepositories().add( local );
        return uni;
    }


    private ReleaseFile getReleaseByName( String release, Repository repository )
    {
        Set<ReleaseFile> distributions = repository.getDistributions();
        for ( ReleaseFile distr : distributions )
        {
            if ( distr.getCodename().equals( release ) )
            {
                return distr;
            }
        }
        return null;
    }


}

