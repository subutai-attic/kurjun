package ai.subut.kurjun.http.snap;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.http.HttpServer;
import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.NonLocalRepository;
import ai.subut.kurjun.model.repository.UnifiedRepository;
import ai.subut.kurjun.model.security.Identity;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.security.service.AuthManager;


/**
 * This servlet is used to test and demonstrate unified repository features.
 * <p>
 * This class will eventually be deleted or moved to appropriate place.
 *
 */
@Singleton
public class SnapUniServlet extends HttpServletBase
{

    @Inject
    private RepositoryFactory repositoryFactory;

    private Identity identity;


    @Override
    public void init() throws ServletException
    {
        this.identity = HttpServer.getIdentity();
    }


    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        NonLocalRepository remote = repositoryFactory.createNonLocalSnap( "http://10.0.3.156:8080/snaps", identity );

        UnifiedRepository uni = repositoryFactory.createUnifiedRepo();
        uni.getRepositories().add( remote );
        uni.getRepositories().add( repositoryFactory.createLocalSnap( HttpServer.CONTEXT ) );


        DefaultMetadata meta = new DefaultMetadata();
        meta.setMd5sum( getMd5ParameterValue( req ) );
        meta.setName( req.getParameter( NAME_PARAM ) );
        meta.setVersion( req.getParameter( VERSION_PARAM ) );

        SerializableMetadata m = uni.getPackageInfo( meta );
        if ( m != null )
        {
            // TODO: add dedicated path to get package file
            if ( req.getParameter( "file" ) != null )
            {
                try ( InputStream is = uni.getPackageStream( m );
                      OutputStream os = resp.getOutputStream() )
                {
                    IOUtils.copy( is, os );
                }
            }
            else
            {
                resp.setContentType( "application/json" );
                try ( PrintWriter writer = resp.getWriter() )
                {
                    writer.print( m.serialize() );
                }
            }
        }
        else
        {
            notFound( resp, "Package not found." );
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


}

