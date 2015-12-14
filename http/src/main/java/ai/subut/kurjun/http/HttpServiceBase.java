package ai.subut.kurjun.http;


import java.util.Optional;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.model.security.Permission;
import ai.subut.kurjun.security.service.AuthManager;


/**
 * Abstract base class for HTTP service classes implemented using JAX-RS. This class has an authentication method to be
 * used in all other implementations.
 *
 */
abstract class HttpServiceBase
{

    /**
     * Fingerprint can be either set in request header or as a query parameter. This field holds value in header.
     */
    @HeaderParam( KurjunConstants.HTTP_HEADER_FINGERPRINT )
    protected String fingerprintHeader;

    /**
     * Fingerprint can be either set in request header or as a query parameter. This field holds query parameter value.
     */
    @QueryParam( KurjunConstants.HTTP_PARAM_FINGERPRINT )
    protected String fingerprintParam;


    protected abstract Logger getLogger();


    /**
     * Gets Kurjun context for the current request.
     *
     * @return
     */
    protected abstract KurjunContext getContext();


    /**
     * Gets authentication manager.
     *
     * @return
     */
    protected abstract AuthManager getAuthManager();


    /**
     * Checks if current request is eligible for supplied action.
     *
     * @param permission permission to check for
     * @return {@code true} if permission is allowed; {@code false} otherwise
     */
    protected boolean checkAuthentication( Permission permission )
    {
        if ( getAuthManager() != null )
        {
            String f = Optional.ofNullable( fingerprintHeader ).orElse( fingerprintParam );
            return getAuthManager().isAllowed( f, permission, getContext().getName() );
        }
        // if auth manager is not set, that means we do not have to check authentiaction
        return true;
    }


    /**
     * Decodes supplied md5 checksum to binary. If supplied md5 is invalid, {@code null} is returned without throwing
     * exceptions. This method is useful for request handling methods that expect md5 checksum values.
     *
     * @param md5 strin md5 checksum
     * @return binary form of md5 checksum
     */
    protected byte[] decodeMd5Param( String md5 )
    {
        try
        {
            return Hex.decodeHex( md5.toCharArray() );
        }
        catch ( DecoderException ex )
        {
            getLogger().info( "Invalid md5 checksum", ex );
            return null;
        }
    }


    protected Response notFoundResponse( String msg )
    {
        return Response.status( Response.Status.NOT_FOUND ).entity( msg ).build();
    }


    protected Response packageNotFoundResponse()
    {
        return notFoundResponse( "Package not found." );
    }


    protected Response forbiddenResponse( String msg )
    {
        return Response.status( Response.Status.FORBIDDEN ).entity( msg ).build();
    }


    protected Response forbiddenResponse()
    {
        return forbiddenResponse( "No permission." );
    }


}

