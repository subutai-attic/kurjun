package ai.subut.kurjun.web.controllers.rest;


import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.metadata.common.subutai.TemplateId;
import ai.subut.kurjun.metadata.common.utils.IdValidators;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.web.controllers.BaseController;
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

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * REST Controller for Template Management
 */

@Singleton
public class RestTemplateController extends BaseController
{

    private static final Logger LOGGER = LoggerFactory.getLogger( RestTemplateController.class );

    @Inject
    TemplateManagerService templateManagerService;


    @FileProvider( SubutaiFileHandler.class )
    public Result upload( Context context, @Param( "repository" ) String repository, @Param( "file" ) FileItem file,
                          @Param( "md5" ) String md5 ) throws Exception
    {
        if ( repository == null )
        {
            repository = "public";
        }

        KurjunFileItem fileItem = ( KurjunFileItem ) file;

        if ( md5 != null && !md5.isEmpty() )
        {
            if ( !fileItem.md5().equals( md5 ) )
            {
                fileItem.cleanup();
                return Results.badRequest().render( "MD5 checksum miss match" ).text();
            }
        }

        //*****************************************************
        templateManagerService.setUserSession( (UserSession ) context.getAttribute( "USER_SESSION" ) );
        String id = templateManagerService.upload( repository, fileItem.getInputStream() );
        //*****************************************************

        String[] temp = id.split( "\\." );
        //temp contains [fprint].[md5]
        if ( temp.length == 2 )
        {
            return Results.ok().render( id ).text();
        }

        return Results.internalServerError().render( "Server error" ).text();
    }


    public Result info( Context context, @Param( "id" ) String id, @Param( "name" ) String name, @Param( "version" ) String version,
                        @Param( "md5" ) String md5, @Param( "type" ) String type )
    {
        TemplateId tid = null;
        DefaultTemplate defaultTemplate = null;

        //*****************************************************
        templateManagerService.setUserSession( (UserSession ) context.getAttribute( "USER_SESSION" ) );
        //*****************************************************

        if ( id != null )
        {
            tid = IdValidators.Template.validate( id );
            defaultTemplate = templateManagerService.getTemplate( tid, md5, name, version );
        }
        else
        {
            defaultTemplate = templateManagerService.getTemplate( null, md5, name, version );
        }
        if ( defaultTemplate == null )
        {
            return Results.noContent();
        }
        if ( type != null && !type.isEmpty() )
        {
            switch ( type )
            {
                case "text":
                    return Results.ok().render( defaultTemplate.getId() ).text();
                default:
                    return Results.ok().render( defaultTemplate ).json();
            }
        }
        
        return Results.ok().render( defaultTemplate ).json();
    }


    public Result download( Context context, @Param( "id" ) String templateId ) throws InternalServerErrorException
    {
        checkNotNull( templateId, "Template ID cannot be null" );

        TemplateId tid = IdValidators.Template.validate( templateId );

        Renderable renderable = null;
        try
        {
            //*****************************************************
            templateManagerService.setUserSession( (UserSession ) context.getAttribute( "USER_SESSION" ) );
            renderable = templateManagerService.renderableTemplate( tid.getOwnerFprint(), tid.getMd5(), false );
            //*****************************************************
        }
        catch ( IOException e )
        {
            e.printStackTrace();

            throw new InternalServerErrorException( "Internal server error." );
        }

        return new Result( 200 ).render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
    }


    public Result delete( Context context, @Param( "id" ) String templateId )
    {
        checkNotNull( templateId, "Template ID cannot be null" );

        TemplateId tid = IdValidators.Template.validate( templateId );

        boolean success;

        try
        {
            //*****************************************************
            templateManagerService.setUserSession( (UserSession ) context.getAttribute( "USER_SESSION" ) );
            success = templateManagerService.delete( tid );
            //*****************************************************
        }
        catch ( IOException e )
        {
            e.printStackTrace();

            throw new InternalServerErrorException( "Error while deleting artifact" );
        }

        if(success)
            return Results.ok().render( String.format( "Deleted: %b", success ) ).text();
        else
            return Results.forbidden();
    }


    public Result list( Context context, @Param( "repository" ) String repository )
    {
        try
        {
            if ( repository == null )
            {
                repository = "public";
            }

            //*****************************************************
            templateManagerService.setUserSession( (UserSession ) context.getAttribute( "USER_SESSION" ) );
            List<SerializableMetadata> defaultTemplateList = templateManagerService.list( repository, false );
            //*****************************************************

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
