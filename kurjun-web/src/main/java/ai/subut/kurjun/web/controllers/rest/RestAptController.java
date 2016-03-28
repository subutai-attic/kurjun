package ai.subut.kurjun.web.controllers.rest;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import com.google.inject.Inject;

import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.web.controllers.BaseController;
import ai.subut.kurjun.web.handler.SubutaiFileHandler;
import ai.subut.kurjun.web.service.impl.AptManagerServiceImpl;
import ai.subut.kurjun.web.utils.Utils;
import ninja.Context;
import ninja.Renderable;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.uploads.FileItem;
import ninja.uploads.FileProvider;


/**
 * REST Controller for Apt Management
 */
public class RestAptController extends BaseController
{

    @Inject
    private AptManagerServiceImpl managerService;


    @FileProvider( SubutaiFileHandler.class )
    public Result upload( Context context, @Param( "file" ) FileItem file,
                          @Param( "global_kurjun_sptoken" ) String globalKurjunToken ) throws IOException
    {

        File filename = file.getFile();
        try ( InputStream inputStream = new FileInputStream( file.getFile() ) )
        {
            //********************************************
            UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
            URI uri = managerService.upload(uSession, inputStream );
            //********************************************

            return Results.ok().render( uri ).text();
        }
    }


    public Result release( Context context, @PathParam( "release" ) String release,
                           @Param( "global_kurjun_sptoken" ) String globalKurjunToken )
    {
        //        checkNotNull( release, "Release cannot be null" );

        //********************************************
        String rel = managerService.getRelease( release, null, null );
        //********************************************

        if ( rel != null )
        {
            return Results.ok().render( rel ).text();
        }

        return Results.notFound().render( String.format( "Not found:%s", release ) );
    }


    public Result packageIndexes( Context context, @PathParam( "release" ) String release,
                                  @PathParam( "component" ) String component, @PathParam( "arch" ) String arch,
                                  @PathParam( "packages" ) String packagesIndex,
                                  @Param( "global_kurjun_sptoken" ) String globalKurjunToken )
    {
        //        checkNotNull( release, "Release cannot be null" );
        //        checkNotNull( component, "Component cannot be null" );
        //        checkNotNull( arch, "Arch cannot be null" );
        //        checkNotNull( packagesIndex, "Package Index cannot be null" );

        //********************************************
        Renderable renderable = managerService.getPackagesIndex( release, component, arch, packagesIndex );
        //********************************************

        return Results.ok().render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
    }


    public Result getPackageByFileName( @PathParam( "filename" ) String filename )
    {
        //        checkNotNull( filename, "File name cannot be null" );

        //********************************************
        Renderable renderable = managerService.getPackageByFilename( filename );
        //********************************************

        return Results.ok().render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
    }


    public Result info( @Param( "md5" ) String md5, @Param( "name" ) String name,
                        @Param( "version" ) String version )

    {
        //        checkNotNull( md5, "MD5 cannot be null" );
        //        checkNotNull( name, "Name cannot be null" );
        //        checkNotNull( version, "Version not found" );

        //********************************************
        String metadata = managerService.getPackageInfo( Utils.MD5.toByteArray( md5 ), name, version );
        //********************************************

        if ( metadata != null )
        {
            return Results.ok().render( metadata ).text();
        }
        return Results.ok().render( "Not found with details provided" );
    }


    public Result download( @Param( "md5" ) String md5 )
    {
        //        checkNotNull( md5, "MD5 cannot be null" );

        //********************************************
        Renderable renderable = managerService.getPackage( Utils.MD5.toByteArray( md5 ) );
        //********************************************

        if ( renderable != null )
        {
            return Results.ok().render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
        }
        return Results.text().render( "Not found with MD5: " + md5 );
    }


    public Result list(  @Param( "type" ) String type, @Param( "repository" ) String repository )
    {
        if ( repository == null )
        {
            repository = "all";
        }

        //********************************************
        List<SerializableMetadata> serializableMetadataList = managerService.list( repository );
        //********************************************

        if ( serializableMetadataList != null )
        {
            if ( type != null && type.equals( "text" ) )
            {
                return Results.ok().render( serializableMetadataList ).text();
            }
            return Results.ok().render( serializableMetadataList ).json();
        }

        return Results.text();
    }


    public Result delete( Context context, @Param( "md5" ) String md5 )
    {
        //        checkNotNull( md5, "MD5 cannot be null" );

        //********************************************
        UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        boolean success = managerService.delete( uSession, Utils.MD5.toByteArray( md5 ) );
        //********************************************

        if ( success )
        {
            return Results.ok().render( "Deleted: " + success ).text();
        }
        return Results.notFound().render( "Not found: " + md5 ).text();
    }


    public Result md5()
    {
        return Results.ok().render( managerService.md5() ).text();
    }
}
