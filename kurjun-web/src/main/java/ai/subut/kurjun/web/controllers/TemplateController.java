package ai.subut.kurjun.web.controllers;


import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.web.handler.SubutaiFileHandler;
import ai.subut.kurjun.web.model.KurjunFileItem;
import ai.subut.kurjun.web.service.TemplateManagerService;
import ninja.Context;
import ninja.Renderable;
import ninja.Result;
import ninja.Results;
import ninja.exceptions.InternalServerErrorException;
import ninja.params.Param;
import ninja.uploads.FileItem;
import ninja.uploads.FileProvider;


/**
 * REST Controller for Template Management
 */

@Singleton
public class TemplateController extends BaseController
{

    private static final Logger LOGGER = LoggerFactory.getLogger( TemplateController.class );

    @Inject
    TemplateManagerService templateManagerService;


    @FileProvider( SubutaiFileHandler.class )
    public Result upload( Context context, @Param( "id" ) String fingerprint, @Param( "file" ) FileItem file,
                          @Param( "md5" ) String md5 ) throws Exception
    {
        if ( fingerprint == null )
        {
            fingerprint = "public";
        }

        KurjunFileItem fileItem = ( KurjunFileItem ) file;

        if ( fileItem.md5().equals( md5 ) )
        {
            fileItem.cleanup();
            return Results.badRequest().render( "MD5 checksum miss match" );
        }

        String id = templateManagerService.upload( fingerprint, fileItem.getInputStream() );

        String[] temp = id.split( "\\." );
        //temp contains [fprint].[md5]
        if ( temp.length == 2 )
        {
            if ( !temp[1].equalsIgnoreCase( md5 ) )
            {
                return Results.badRequest().render( "MD5 checksum miss match" );
            }
        }
        return Results.ok().render( id ).text();
    }


    //    public Result info( @Param( "id" ) String fingerprint, @Param( "name" ) String name,
    //                        @Param( "version" ) String version )
    //    {
    //        if ( fingerprint == null )
    //        {
    //            fingerprint = "public";
    //        }
    //
    //    }


    public Result download( Context context, @Param( "id" ) String fingerprint, @Param( "md5" ) String md5 )
            throws InternalServerErrorException
    {
        Renderable renderable = null;
        try
        {
            renderable = templateManagerService.renderableTemplate( fingerprint, md5, false );
        }
        catch ( IOException e )
        {
            e.printStackTrace();

            throw new InternalServerErrorException( "Internal server error." );
        }
        return new Result( 200 ).render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
    }


    public Result delete( Context context, @Param( "md5" ) String md5, @Param( "id" ) String fingerprint )
    {
        boolean success;

        if ( fingerprint == null )
        {
            fingerprint = "public";
        }

        try
        {
            success = templateManagerService.delete( md5, fingerprint );
        }
        catch ( IOException e )
        {
            e.printStackTrace();

            throw new InternalServerErrorException( "Error while deleting artifact" );
        }

        return Results.ok().render( String.format( "Deleted: %b", success ) ).text();
    }


    public Result list( Context context, @Param( "id" ) String fingerprint )
    {
        try
        {
            if ( fingerprint == null )
            {
                fingerprint = "public";
            }

            List<SerializableMetadata> defaultTemplateList = templateManagerService.list( fingerprint, false );

            return Results.ok().render( defaultTemplateList ).json();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            throw new InternalServerErrorException( "Error while getting list of artifacts" );
        }
    }


    public Result md5()
    {
        return Results.ok().render( templateManagerService.md5() ).text();
    }
}
