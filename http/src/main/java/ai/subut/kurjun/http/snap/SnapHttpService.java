package ai.subut.kurjun.http.snap;


import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;


/**
 * HTTP service for snaps repository. Built using CXF to be compatible with Subutai.
 *
 */
@Path( "snaps" )
public interface SnapHttpService
{

    @GET
    @Path( "info" )
    @Produces( MediaType.APPLICATION_JSON )
    Response getSnapInfo( @QueryParam( SnapServlet.MD5_PARAM ) String md5,
                          @QueryParam( SnapServlet.NAME_PARAM ) String name,
                          @QueryParam( SnapServlet.VERSION_PARAM ) String version );


    @GET
    @Path( "get" )
    Response getSnapFile( @QueryParam( SnapServlet.MD5_PARAM ) String md5 );


    @POST
    @Path( "upload" )
    @Consumes( MediaType.MULTIPART_FORM_DATA )
    Response upload( @Multipart( SnapServlet.PACKAGE_FILE_PART_NAME ) Attachment attachment );


    @DELETE
    @Path( "" )
    Response deleteTemplates( @QueryParam( SnapServlet.MD5_PARAM ) String md5 );
}

