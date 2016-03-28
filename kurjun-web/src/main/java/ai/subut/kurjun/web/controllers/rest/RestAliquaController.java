package ai.subut.kurjun.web.controllers.rest;


import com.google.inject.Inject;

import ai.subut.kurjun.metadata.common.raw.RawMetadata;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.web.controllers.BaseController;
import ai.subut.kurjun.web.handler.SubutaiFileHandler;
import ai.subut.kurjun.web.model.KurjunFileItem;
import ai.subut.kurjun.web.service.RawManagerService;
import ai.subut.kurjun.web.utils.Utils;
import ninja.Context;
import ninja.Renderable;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.uploads.FileItem;
import ninja.uploads.FileProvider;

import static com.google.common.base.Preconditions.checkNotNull;


public class RestAliquaController extends BaseController
{

    @Inject
    private RawManagerService rawManagerService;


    @FileProvider( SubutaiFileHandler.class )
    public Result upload( Context context, @Param( "file" ) FileItem fileItem,
                          @Param( "fingerprint" ) String fingerprint,
                          @Param( "global_kurjun_sptoken" ) String globalKurjunToken )
    {
        String sptoken = "";

        if ( globalKurjunToken != null )
        {
            sptoken = globalKurjunToken;
        }

        //checkNotNull( fileItem, "MD5 cannot be null" );
        if ( fingerprint == null )
        {
            fingerprint = "raw";
        }

        KurjunFileItem kurjunFileItem = ( KurjunFileItem ) fileItem;

        Metadata metadata;

        //********************************************
        UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        metadata = rawManagerService.put( uSession, kurjunFileItem.getFile(), kurjunFileItem.getFileName(), fingerprint );
        //********************************************

        if ( metadata != null )
        {
            return Results.ok().render( metadata.getId() ).text();
        }

        return Results.internalServerError().render( "Could not save file" ).text();
    }


    public Result getFile( Context context, @Param( "id" ) String id,
                           @Param( "global_kurjun_sptoken" ) String globalKurjunToken )
    {
        checkNotNull( id, "ID cannot be null" );
        String sptoken = "";

        if ( globalKurjunToken != null )
        {
            sptoken = globalKurjunToken;
        }

        String[] temp = id.split( "\\." );

        Renderable renderable = null;
        //temp contains [fprint].[md5]
        if ( temp.length == 2 )
        {
            renderable = rawManagerService.getFile( temp[0], Utils.MD5.toByteArray( temp[1] ) );
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
            success = rawManagerService.delete(uSession, temp[0], Utils.MD5.toByteArray( temp[1] ) );
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


    public Result list( @Param( "repository" ) String repository,
                        @Param( "global_kurjun_sptoken" ) String globalKurjunToken )
    {
        if ( repository == null )
        {
            repository = "all";
        }

        return Results.ok().render( rawManagerService.list( repository ) ).json();
    }


    public Result info( @Param( "id" ) String id, @Param( "name" ) String name, @Param( "md5" ) String md5,
                        @Param( "type" ) String type, @Param( "fingerprint" ) String fingerprint,
                        @Param( "global_kurjun_sptoken" ) String globalKurjunToken )
    {
        RawMetadata rawMetadata = new RawMetadata();
        String sptoken = "";

        if ( globalKurjunToken != null )
        {
            sptoken = globalKurjunToken;
        }

        if ( fingerprint == null && md5 != null )
        {
            fingerprint = "raw";
        }
        rawMetadata.setName( name );
        rawMetadata.setMd5Sum( Utils.MD5.toByteArray( md5 ) );
        rawMetadata.setFingerprint( fingerprint );

        Metadata metadata = rawManagerService.getInfo( rawMetadata );

        if ( metadata != null )
        {
            return Results.ok().render( metadata ).json();
        }
        return Results.notFound().render( "Not found" ).text();
    }
}
