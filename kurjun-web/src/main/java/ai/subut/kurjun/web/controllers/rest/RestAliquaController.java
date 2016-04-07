package ai.subut.kurjun.web.controllers.rest;


import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.web.controllers.BaseController;
import ai.subut.kurjun.web.handler.SubutaiFileHandler;
import ai.subut.kurjun.web.model.KurjunFileItem;
import ai.subut.kurjun.web.service.RawManagerService;
import ninja.Context;
import ninja.Renderable;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.uploads.FileItem;
import ninja.uploads.FileProvider;

import static com.google.common.base.Preconditions.checkNotNull;


@Singleton
public class RestAliquaController extends BaseController
{

    @Inject
    private RawManagerService rawManagerService;


    @FileProvider( SubutaiFileHandler.class )
    public Result upload( Context context, @Param( "file" ) FileItem fileItem,
                          @Param( "repository" ) String repository )
    {

        KurjunFileItem kurjunFileItem = ( KurjunFileItem ) fileItem;

        Metadata metadata;

        //********************************************
        UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        metadata =
                rawManagerService.put( uSession, kurjunFileItem.getFile(), kurjunFileItem.getFileName(), repository );
        //********************************************

        if ( metadata != null )
        {
            return Results.ok().render( metadata.getId() ).text();
        }

        return Results.internalServerError().render( "Could not save file" ).text();
    }


    public Result getFile( Context context, @Param( "id" ) String id )
    {
        checkNotNull( id, "ID cannot be null" );


        String[] temp = id.split( "\\." );

        Renderable renderable = null;
        //temp contains [fprint].[md5]
        if ( temp.length == 2 )
        {
            renderable = rawManagerService.getFile( temp[0], temp[1] );
        }
        if ( renderable != null )
        {
            return Results.ok().render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
        }
        return Results.notFound().render( "File not found" ).text();
    }


    public Result delete( Context context, @Param( "id" ) String id,
                          @Param( "global_kurjun_sptoken" ) String globalKurjunToken )
    {
        checkNotNull( id, "ID cannot be null" );
        String[] temp = id.split( "\\." );
        String sptoken = "";

        if ( globalKurjunToken != null )
        {
            sptoken = globalKurjunToken;
        }
        boolean success = false;

        if ( temp.length == 2 )
        {
            //********************************************
            UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
            success = rawManagerService.delete( uSession, temp[0], temp[1] );
            //********************************************
        }

        if ( success )
        {
            return Results.ok().render( id + " deleted" ).text();
        }

        return Results.notFound().render( "Not found" ).text();
    }


    public Result md5()
    {
        return Results.ok().render( rawManagerService.md5() ).text();
    }


    public Result list( @Param( "repository" ) String repository,@Param( "search" ) String search )
    {
        if ( search == null )
        {
            search = "local";
        }

        return Results.ok().render( rawManagerService.list( repository, search ) ).json();
    }



    public Result info( @Param( "repository" ) String repository, @Param( "md5" ) String md5, @Param( "search" ) String search )
    {
        Metadata metadata = rawManagerService.getInfo( repository , md5 , search );

        if ( metadata != null )
        {
            return Results.ok().render( metadata ).json();
        }
        return Results.notFound().render( "Not found" ).text();
    }

}
