package ai.subut.kurjun.http.snap;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.http.HttpServer;
import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.NonLocalRepository;
import ai.subut.kurjun.model.repository.UnifiedRepository;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.security.service.AuthManager;

import static ai.subut.kurjun.http.HttpServletBase.MD5_PARAM;
import static ai.subut.kurjun.http.HttpServletBase.NAME_PARAM;
import static ai.subut.kurjun.http.HttpServletBase.VERSION_PARAM;


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


    @Override
    public void init() throws ServletException
    {
    }


    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        NonLocalRepository remote = repositoryFactory.createNonLocalSnap( "http://10.0.3.156:8080/snaps" );

        UnifiedRepository uni = repositoryFactory.createUnifiedRepo();
        uni.getRepositories().add( remote );
        uni.getRepositories().add( repositoryFactory.createLocalSnap( HttpServer.CONTEXT ) );


        DefaultMetadata meta = new DefaultMetadata();
        meta.setMd5sum( getMd5ParameterValue( req, MD5_PARAM ) );
        meta.setName( req.getParameter( NAME_PARAM ) );
        meta.setVersion( req.getParameter( VERSION_PARAM ) );

        SerializableMetadata m = uni.getPackageInfo( meta );
        if ( m != null )
        {
            resp.setContentType( "application/json" );
            try ( PrintWriter writer = resp.getWriter() )
            {
                writer.print( m.serialize() );
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


    private byte[] getMd5ParameterValue( HttpServletRequest req, String paramName )
    {
        String md5 = req.getParameter( paramName );
        if ( md5 != null )
        {
            try
            {
                return Hex.decodeHex( md5.toCharArray() );
            }
            catch ( DecoderException ex )
            {
            }
        }
        return null;
    }
}

