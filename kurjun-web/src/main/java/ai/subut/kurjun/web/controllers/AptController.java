package ai.subut.kurjun.web.controllers;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.web.service.impl.AptManagerServiceImpl;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.uploads.DiskFileItemProvider;
import ninja.uploads.FileItem;
import ninja.uploads.FileProvider;


/**
 * REST Controller for Apt Management
 */
@Singleton
public class AptController
{
    //@formatter:off
    @Inject
    private AptManagerServiceImpl managerService;


    @FileProvider( DiskFileItemProvider.class )
    public Result upload(@Param("upfile") FileItem file) throws IOException
    {
        try(InputStream inputStream = new FileInputStream( file.getFile() )){
            URI uri = managerService.upload(inputStream);
            return Results.ok().render( uri );
        }

    }


    public Result release( @PathParam( "release" ) String release )
    {
       return Results.ok();
    }


    public Result packageIndexes( @PathParam( "release" )   String release,
                                  @PathParam( "component" ) String component,
                                  @PathParam( "arch" )      String arch,
                                  @PathParam( "packages" ) String packagesIndex )
    {
        return Results.ok();
    }


    public Result getPackageByFileName( @PathParam( "filename" ) String filename )
    {
        return Results.ok();
    }


    public Result info( @Param( "md5" )     String md5,
                        @Param( "name" )    String name,
                        @Param( "version" ) String version )
    {
        return Results.ok();
    }


    public Result get( @Param( "md5" ) String md5 )
    {
        return Results.ok();
    }

    public Result list()
    {
        return Results.ok();
    }

    public Result delete(@Param("md5") String md5)
    {
        return Results.ok();
    }
    //@formatter:on
}
