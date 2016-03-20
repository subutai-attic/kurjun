package ai.subut.kurjun.web.controllers.rest;


import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.metadata.common.raw.RawMetadata;
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
                          @Param( "fingerprint" ) String fingerprint )
    {

        //checkNotNull( fileItem, "MD5 cannot be null" );
        if ( fingerprint == null )
        {
            fingerprint = "raw";
        }

        KurjunFileItem kurjunFileItem = ( KurjunFileItem ) fileItem;

        Metadata metadata;

        metadata = rawManagerService.put( kurjunFileItem.getFile(), kurjunFileItem.getFileName(), fingerprint );

        if ( metadata != null )
        {
            return Results.ok().render( metadata.getId() ).text();
        }

        return Results.internalServerError().render( "Could not save file" ).text();
    }


    public Result getFile( Context context, @Param( "md5" ) String md5 )
    {
        checkNotNull( md5, "MD5 cannot be null" );

        Renderable renderable = rawManagerService.getFile( Utils.MD5.toByteArray( md5 ) );

        if ( renderable != null )
        {
            return Results.ok().render( renderable );
        }
        return Results.notFound().render( "File not found" ).text();
    }


    public Result delete( Context context, @Param( "md5" ) String md5 )
    {
        checkNotNull( md5, "MD5 cannot be null" );

        boolean success = rawManagerService.delete( Utils.MD5.toByteArray( md5 ) );

        if ( success )
        {
            return Results.ok().render( md5 + " deleted" ).text();
        }
        return Results.notFound().text();
    }


    public Result md5()
    {
        return Results.ok().render( rawManagerService.md5() ).text();
    }


    public Result getList()
    {
        return Results.ok().render( rawManagerService.list() ).json();
    }


    public Result info( @Param( "id" ) String id, @Param( "name" ) String name, @Param( "md5" ) String md5,
                        @Param( "type" ) String type, @Param( "fingerprint" ) String fingerprint )
    {
        RawMetadata rawMetadata = new RawMetadata();

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
