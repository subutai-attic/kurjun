package ai.subut.kurjun.http.service;


import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;


/**
 * HTTP service for templates repository. Built using CXF to be compatible with Subutai.
 *
 */
@Path( "templates" )
public interface TemplateHttpService
{

    @GET
    @Path( "{repository}/get" )
    @Produces( MediaType.TEXT_PLAIN )
    Response getTemplate( @PathParam( "repository" ) String repository,
                          @QueryParam( HttpServiceConstants.MD5_PARAM ) String md5,
                          @QueryParam( HttpServiceConstants.NAME_PARAM ) String name,
                          @QueryParam( HttpServiceConstants.VERSION_PARAM ) String version,
                          @QueryParam( HttpServiceConstants.TYPE_PARAM ) String type
    );


    @POST
    @Path( "upload/{repository}" )
    @Produces( MediaType.TEXT_PLAIN )
    @Consumes( MediaType.MULTIPART_FORM_DATA )
    Response uploadTemplate( @PathParam( "repository" ) String repository,
                             @Multipart( HttpServiceConstants.PACKAGE_FILE_PART_NAME ) Attachment attachment
    );


    @DELETE
    @Path( "{repository}" )
    @Produces( MediaType.TEXT_PLAIN )
    Response deleteTemplates( @PathParam( "repository" ) String repository,
                              @QueryParam( HttpServiceConstants.MD5_PARAM ) String md5
    );
}

