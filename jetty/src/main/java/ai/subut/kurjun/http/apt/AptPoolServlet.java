package ai.subut.kurjun.http.apt;


import java.io.IOException;
import java.io.InputStream;

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
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.security.Permission;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.repo.service.PackageFilenameBuilder;
import ai.subut.kurjun.repo.service.PackageFilenameParser;
import ai.subut.kurjun.security.service.AuthManager;


@Singleton
class AptPoolServlet extends HttpServletBase
{

    @Inject
    private RepositoryFactory repositoryFactory;

    @Inject
    private AuthManager authManager;

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
        if ( !checkAuthentication( req, Permission.GET_PACKAGE ) )
        {
            forbidden( resp );
            return;
        }

        String path = "/pool" + req.getPathInfo();
        String packageName = filenameParser.getPackageFromFilename( path );
        String version = filenameParser.getVersionFromFilename( path );
        if ( packageName == null || version == null )
        {
            badRequest( resp, "Invalid pool path" );
            return;
        }

        DefaultMetadata m = new DefaultMetadata();
        m.setName( packageName );
        m.setVersion( version );

        LocalRepository repo = repositoryFactory.createLocalApt( context );
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


}

