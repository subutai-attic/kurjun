package ai.subut.kurjun.web.controllers;


import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.web.handler.SubutaiFileHandler;
import ai.subut.kurjun.web.model.KurjunFileItem;
import ai.subut.kurjun.web.service.RawManagerService;
import ninja.Context;
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
    public Result upload( Context context, @Param( "file" ) FileItem fileItem, @Param( "md5" ) String md5 )
    {
        checkNotNull( md5, "MD5 cannot be null" );
        checkNotNull( fileItem, "MD5 cannot be null" );

        KurjunFileItem kurjunFileItem = ( KurjunFileItem ) fileItem;
        boolean success = false;

        if ( kurjunFileItem.md5().equals( toByteArray( md5 ) ) )
        {
            success = rawManagerService.put( kurjunFileItem.getFile() );
        }


    }


    public Result getFile( @Param( "md5" ) String md5 )
    {
        rawManagerService.getFile( toByteArray( md5 ) );
        return null;
    }


    public Result getList()
    {
        return Results.ok().render( rawManagerService.list() ).json();
    }


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
