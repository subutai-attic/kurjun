package ai.subut.kurjun.http.local;


import javax.ws.rs.Consumes;
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


@Path( "vapt" )
public interface AptHttpService
{

    @GET
    @Path( "dists/{release}/{component}/{arch: binary-\\w+}/Release" )
    @Produces( MediaType.TEXT_PLAIN )
    Response getRelease(
            @PathParam( "release" ) String release,
            @PathParam( "component" ) String component,
            @PathParam( "arch" ) String arch
    );


    @GET
    @Path( "dists/{release}/{component}/{arch: binary-\\w+}/{packages: Packages(\\.\\w+)?}" )
    @Produces( MediaType.TEXT_PLAIN )
    Response getPackagesIndex(
            @PathParam( "release" ) String release,
            @PathParam( "component" ) String component,
            @PathParam( "arch" ) String arch,
            @PathParam( "packages" ) String packagesIndex
    );


    @GET
    @Path( "pool/{filename: .+}" )
    @Produces( MediaType.APPLICATION_OCTET_STREAM )
    Response getPackageByFilename( @PathParam( "filename" ) String filename );


    @GET
    @Path( "info" )
    @Produces( MediaType.APPLICATION_JSON )
    Response getPackageInfo( @QueryParam( AptRepoServlet.MD5_PARAM ) String md5,
                             @QueryParam( AptRepoServlet.NAME_PARAM ) String name,
                             @QueryParam( AptRepoServlet.VERSION_PARAM ) String version );


    @GET
    @Path( "get" )
    Response getPackage( @QueryParam( AptRepoServlet.MD5_PARAM ) String md5 );


    @POST
    @Path( "upload" )
    @Consumes( MediaType.MULTIPART_FORM_DATA )
    Response upload( @Multipart( AptRepoServlet.PACKAGE_FILE_PART_NAME ) Attachment attachment );

}

