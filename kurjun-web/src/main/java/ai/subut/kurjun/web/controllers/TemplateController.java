package ai.subut.kurjun.web.controllers;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.web.service.TemplateManagerService;
import ninja.Context;
import ninja.Renderable;
import ninja.Result;
import ninja.Results;
import ninja.exceptions.InternalServerErrorException;
import ninja.params.Param;
import ninja.uploads.DiskFileItemProvider;
import ninja.uploads.FileItem;
import ninja.uploads.FileProvider;


/**
 * REST Controller for Template Management
 */

@Singleton
public class TemplateController
{

    @Inject
    TemplateManagerService templateManagerService;


    @FileProvider( DiskFileItemProvider.class )
    public Result upload( Context context, @Param( "fingerprint" ) String fingerprint,
                          @Param( "upfile" ) FileItem upfile, @Param( "md5" ) String md5 ) throws Exception
    {

        InputStream inputStream = upfile.getInputStream();

        String id = templateManagerService.upload( fingerprint, inputStream );

        if ( id.split( "." )[1].equalsIgnoreCase( md5 ) )
        {
            return Results.badRequest().render( "MD5 checksum miss match" );
        }

        return Results.ok().render( id );
    }


    public Result download( Context context, @Param( "fingerprint" ) String fingerprint, @Param( "md5" ) String md5 )
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


    public Result delete( Context context, @Param( "md5" ) String md5 )
    {
        boolean success;

        try
        {
            success = templateManagerService.delete( md5 );
        }
        catch ( IOException e )
        {
            e.printStackTrace();

            throw new InternalServerErrorException( "Error while deleting artifact" );
        }

        return Results.ok().render( String.format( "Deleted: %b", success ) );
    }


    public Result list( Context context, @Param( "fingerprint" ) String fingerprint )
    {
        try
        {
            if ( fingerprint == null )
            {
                fingerprint = "public";
            }
            List<SerializableMetadata> defaultTemplateList = templateManagerService.list( fingerprint, false );

            return Results.ok().json().render( defaultTemplateList );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            throw new InternalServerErrorException( "Error while getting list of artifacts" );
        }
    }
}
