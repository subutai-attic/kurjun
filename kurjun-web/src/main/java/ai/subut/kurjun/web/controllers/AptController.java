package ai.subut.kurjun.web.controllers;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.web.service.impl.AptManagerServiceImpl;
import ninja.Renderable;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.uploads.DiskFileItemProvider;
import ninja.uploads.FileItem;
import ninja.uploads.FileProvider;

import static com.google.common.base.Preconditions.checkNotNull;


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
        checkNotNull(release, "Release cannot be null");

        String rel = managerService.getRelease( release ,null ,null );

        if ( rel != null )
        {
            return Results.ok().render( rel );
        }

        return Results.notFound().render( String.format("Not found:%s",release));

    }


    public Result packageIndexes( @PathParam( "release" )   String release,
                                  @PathParam( "component" ) String component,
                                  @PathParam( "arch" )      String arch,
                                  @PathParam( "packages" ) String packagesIndex )
    {
        checkNotNull(release, "Release cannot be null");
        checkNotNull(component,"Component cannot be null");
        checkNotNull(arch,"Arch cannot be null");
        checkNotNull(packagesIndex,"Package Index cannot be null");

        Renderable renderable = managerService.getPackagesIndex( release,component,arch,packagesIndex );

        return Results.ok().render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
    }


    public Result getPackageByFileName( @PathParam( "filename" ) String filename )
    {
        checkNotNull( filename,"File name cannot be null" );

        Renderable renderable = managerService.getPackageByFilename( filename );

        return Results.ok().render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
    }


    public Result info( @Param( "md5" )     String md5,
                        @Param( "name" )    String name,
                        @Param( "version" ) String version )

    {
        checkNotNull(md5,"MD5 cannot be null");
        checkNotNull(name,"Name cannot be null");
        checkNotNull( version,"Version not found" );

        String metadata = managerService.getPackageInfo(toByteArray( md5 ),name,version);
        if ( metadata != null )
        {
            return Results.ok().render( metadata );
        }
        return Results.ok().render( "Not found with details provided" );
    }


    public Result get( @Param( "md5" ) String md5 )
    {
        checkNotNull( md5,"MD5 cannot be null" );

        Renderable renderable = managerService.getPackage( toByteArray( md5 ) );

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


    private byte[] toByteArray( String md5 )
    {
        if ( md5 != null )
        {
            try
            {
                return Hex.decodeHex( md5.toCharArray() );
            }
            catch ( DecoderException ex )
            {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
