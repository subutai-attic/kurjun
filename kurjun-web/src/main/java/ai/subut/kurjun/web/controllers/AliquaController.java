package ai.subut.kurjun.web.controllers;


import java.math.BigInteger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.model.metadata.Metadata;
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


@Singleton
public class AliquaController
{

    @Inject
    private RawManagerService rawManagerService;


    @FileProvider( SubutaiFileHandler.class )
    public Result upload( Context context, @Param( "file" ) FileItem fileItem )
    {

        checkNotNull( fileItem, "MD5 cannot be null" );

        KurjunFileItem kurjunFileItem = ( KurjunFileItem ) fileItem;

        Metadata metadata;

        metadata = rawManagerService.put( kurjunFileItem.getFile() );

        if ( metadata != null )
        {
            return Results.ok()
                          .render( metadata.getName() + "." + new BigInteger( 1, metadata.getMd5Sum() ).toString( 16 ) )
                          .text();
        }

        return Results.internalServerError().render( "Could not save file" ).text();
    }


    public Result getFile( @Param( "md5" ) String md5 )
    {
        checkNotNull( md5, "MD5 cannot be null" );

        Renderable renderable = rawManagerService.getFile( Utils.MD5.toByteArray( md5 ) );

        if ( renderable != null )
        {
            return Results.ok().render( renderable );
        }
        return Results.notFound().render( "File not found" ).text();
    }


    public Result delete( @Param( "md5" ) String md5 )
    {
        checkNotNull( md5, "MD5 cannot be null" );

        boolean success = rawManagerService.delete( Utils.MD5.toByteArray( md5 ) );

        if ( success )
        {
            return Results.ok().render( md5 + " deleted" ).text();
        }
        return Results.notFound();
    }


    public Result md5()
    {
        return Results.ok().render( rawManagerService.md5() ).text();
    }


    public Result getList()
    {
        return Results.ok().render( rawManagerService.list() ).json();
    }
}
