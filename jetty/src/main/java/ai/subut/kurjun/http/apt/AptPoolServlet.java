package ai.subut.kurjun.http.apt;


import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.http.HttpServer;
import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.security.Permission;
import ai.subut.kurjun.quota.DataUnit;
import ai.subut.kurjun.quota.QuotaException;
import ai.subut.kurjun.quota.QuotaInfoStore;
import ai.subut.kurjun.quota.QuotaManagerFactory;
import ai.subut.kurjun.quota.transfer.TransferQuota;
import ai.subut.kurjun.quota.transfer.TransferQuotaManager;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.repo.service.PackageFilenameBuilder;
import ai.subut.kurjun.repo.service.PackageFilenameParser;
import ai.subut.kurjun.security.service.AuthManager;
import ai.subut.kurjun.storage.factory.FileStoreFactory;


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

    @Inject
    private QuotaManagerFactory quotaManagerFactory;

    @Inject
    private Injector injector;


    private KurjunContext context;


    @Override
    public void init() throws ServletException
    {
        this.context = HttpServer.CONTEXT;

        TransferQuota quota = new TransferQuota();
        quota.setThreshold( 5 );
        quota.setUnit( DataUnit.MB );
        quota.setTime( 5 );
        quota.setTimeUnit( TimeUnit.MINUTES );

        QuotaInfoStore quotaInfoStore = injector.getInstance( QuotaInfoStore.class );
        try
        {
            quotaInfoStore.saveTransferQuota( quota, context );
        }
        catch ( IOException ex )
        {
            throw new ServletException( ex );
        }
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
            FileStoreFactory fsFactory = injector.getInstance( FileStoreFactory.class );
            long size = fsFactory.create( context ).sizeOf( metadata.getMd5Sum() );

            TransferQuotaManager quotaManager = quotaManagerFactory.createTransferQuotaManager( context );
            if ( !quotaManager.isAllowedToTransfer( size ) )
            {
                internalServerError( resp, "Downloading this file will exceed transfer quota threshold." );
                return;
            }

            try ( InputStream is = repo.getPackageStream( metadata ) )
            {
                if ( is != null )
                {
                    DefaultPackageMetadata pm = gson.fromJson( metadata.serialize(), DefaultPackageMetadata.class );
                    String filename = filenameBuilder.makePackageFilename( pm );
                    resp.setHeader( "Content-Disposition", "attachment; filename=" + filename );
                    quotaManager.copy( is, resp.getOutputStream() );
                }
                else
                {
                    notFound( resp, "Package file not found" );
                }
            }
            catch ( QuotaException ex )
            {
                throw new IOException( ex );
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

