package ai.subut.kurjun.web.controllers.rest;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.web.controllers.BaseAptController;
import ai.subut.kurjun.web.handler.SubutaiFileHandler;
import ai.subut.kurjun.web.service.impl.AptManagerServiceImpl;
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
@Singleton
public class RestAptController extends BaseAptController
{

    @Inject
    private AptManagerServiceImpl managerService;


    @FileProvider( SubutaiFileHandler.class )
    public Result upload( Context context, @Param( "repository" ) String repository, @Param( "file" ) FileItem file )
            throws IOException
    {

        File filename = file.getFile();
        try ( InputStream inputStream = new FileInputStream( file.getFile() ) )
        {
            //********************************************
            UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
            URI uri = managerService.upload( uSession, repository ,inputStream );
            //********************************************

            return Results.ok().render( uri ).text();
        }
    }


    public Result release( Context context, @PathParam( "release" ) String release,
                           @PathParam( "repository" ) String repository )
    {
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
                                  @PathParam( "packages" ) String packagesIndex)
    {
        //********************************************
        Renderable renderable = managerService.getPackagesIndex( release, component, arch, packagesIndex );
        //********************************************

        return Results.ok().render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
    }


    public Result getPackageByFileName( @PathParam( "filename" ) String filename )
    {

        //********************************************
        Renderable renderable = managerService.getPackageByFilename( filename );
        //********************************************

        return Results.ok().render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
    }


    public Result info( Context context , @Param( "repository" ) String repository, @Param( "md5" ) String md5, @Param( "name" ) String name, @Param( "version" ) String version )

    {

        //********************************************
        UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        String metadata = managerService.getPackageInfo(uSession , repository, md5, name, version );
        //********************************************

        if ( metadata != null )
        {
            return Results.ok().render( metadata ).text();
        }
        return Results.ok().render( "Not found with details provided" );
    }


    public Result download( Context context , @Param("repository") String repo, @Param( "md5" ) String md5 )
    {
        //        checkNotNull( md5, "MD5 cannot be null" );

        //********************************************
        UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        Renderable renderable = managerService.getPackage( uSession,repo, md5 );
        //********************************************

        if ( renderable != null )
        {
            return Results.ok().render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
        }
        return Results.text().render( "Not found with MD5: " + md5 );
    }


    public Result list( Context context , @Param( "type" ) String type, @Param( "repository" ) String repository, @Param( "node" ) String node )
    {
        node = StringUtils.isBlank( node ) ? "local" : node;
        repository = StringUtils.isBlank( repository )? AptManagerServiceImpl.REPO_NAME:repository;

        //********************************************
        UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        List<SerializableMetadata> serializableMetadataList = managerService.list( uSession, repository, node );
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


    public Result delete( Context context, @Param( "repository" ) String repository, @Param( "md5" ) String md5 )
    {
        //********************************************
        UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        boolean success = managerService.delete( uSession, repository, md5 );
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
