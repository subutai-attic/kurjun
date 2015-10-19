package ai.subut.kurjun.http.subutai;


import java.io.InputStream;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;


@Path( "templates" )
public interface HttpService
{

    @GET
    @Path( "{type}/get" )
    @Produces( "text/plain" )
    Response getTemplate( @QueryParam( TemplateServlet.MD5_PARAM ) String md5,
                          @QueryParam( TemplateServlet.NAME_PARAM ) String name,
                          @QueryParam( TemplateServlet.VERSION_PARAM ) String version,
                          @QueryParam( TemplateServlet.TYPE_PARAM ) String type );


    @POST
    @Path( "upload/{type}" )
    @Produces( "text/plain" )
    Response uploadTemplate( @Multipart( TemplateUploadServlet.PACKAGE_FILE_PART_NAME ) InputStream is );


    @DELETE
    @Path( "{type}" )
    @Produces( "text/plain" )
    Response deleteTemplates( @QueryParam( TemplateServlet.MD5_PARAM ) String md5 );
}

