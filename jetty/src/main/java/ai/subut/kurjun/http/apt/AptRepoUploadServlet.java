package ai.subut.kurjun.http.apt;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.FileUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.http.HttpServer;
import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.http.ServletUtils;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.security.Permission;
import ai.subut.kurjun.quota.DataUnit;
import ai.subut.kurjun.quota.QuotaException;
import ai.subut.kurjun.quota.QuotaInfoStore;
import ai.subut.kurjun.quota.QuotaManagerFactory;
import ai.subut.kurjun.quota.disk.DiskQuota;
import ai.subut.kurjun.quota.disk.DiskQuotaManager;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.security.service.AuthManager;


@Singleton
@MultipartConfig
class AptRepoUploadServlet extends HttpServletBase
{

    @Inject
    private AuthManager authManager;

    @Inject
    private RepositoryFactory repositoryFactory;

    @Inject
    private QuotaManagerFactory quotaManagerFactory;

    @Inject
    private QuotaInfoStore quotaInfoStore;

    private KurjunContext context;


    @Override
    public void init() throws ServletException
    {
        context = HttpServer.CONTEXT;

        DiskQuota diskQuota = new DiskQuota();
        diskQuota.setThreshold( 5 );
        diskQuota.setUnit( DataUnit.MB );
        try
        {
            quotaInfoStore.saveDiskQuota( diskQuota, context );
        }
        catch ( IOException ex )
        {
            throw new ServletException( ex );
        }
    }


    @Override
    protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        if ( !checkAuthentication( req, Permission.ADD_PACKAGE ) )
        {
            forbidden( resp );
            return;
        }
        if ( !ServletUtils.isMultipart( req ) )
        {
            badRequest( resp, "Not a multipart request" );
            return;
        }

        ServletUtils.setMultipartConfig( req, this.getClass() );

        Part part = req.getPart( PACKAGE_FILE_PART_NAME );
        if ( part == null )
        {
            String msg = String.format( "No package attached with name '%s'", PACKAGE_FILE_PART_NAME );
            badRequest( resp, msg );
            return;
        }


        LocalRepository repository = repositoryFactory.createLocalApt( context );
        DiskQuotaManager diskQuotaManager = quotaManagerFactory.createDiskQuotaManager( context );

        Path dump = null;
        try ( InputStream is = part.getInputStream() )
        {
            dump = diskQuotaManager.copyStream( is );
            try ( InputStream fis = new FileInputStream( dump.toFile() ) )
            {
                repository.put( fis );
            }
        }
        catch ( QuotaException ex )
        {
            internalServerError( resp, "Uploading this package exceeds disk quota." );
        }
        finally
        {
            if ( dump != null )
            {
                FileUtils.deleteQuietly( dump.toFile() );
            }
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

