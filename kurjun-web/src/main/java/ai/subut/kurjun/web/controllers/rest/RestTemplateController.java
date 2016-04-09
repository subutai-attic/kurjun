package ai.subut.kurjun.web.controllers.rest;


import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.metadata.common.subutai.TemplateId;
import ai.subut.kurjun.metadata.common.utils.IdValidators;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.template.TemplateData;
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
        UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        String id = templateManagerService.upload( uSession, repository, fileItem.getInputStream() );
        //*****************************************************

        if ( !Strings.isNullOrEmpty( id ) )
        {
            return Results.ok().render( id ).text();
        }
        else
        {
            return Results.internalServerError().render( "Server error" ).text();
        }
    }


    public Result info( Context context, @Param( "repository" ) String repository, @Param( "name" ) String name,
                        @Param( "version" ) String version, @Param( "md5" ) String md5, @Param( "node" ) String node )

    {
        TemplateData templateData = null;

        //*****************************************************
        UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        //*****************************************************

        templateData = templateManagerService.getTemplate( uSession, repository, md5, name, version, node );

        if ( templateData == null )
        {
            return Results.noContent();
        }


        return Results.ok().render( templateData ).json();
    }


    public Result download( Context context, @Param( "repository" ) String repository, @Param( "md5" ) String md5 )
    {
        Renderable renderable = null;
        try
        {
            //*****************************************************
            UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
            renderable = templateManagerService.renderableTemplate( uSession, repository, md5, false );
            //*****************************************************
        }
        catch ( IOException e )
        {
            e.printStackTrace();

            throw new InternalServerErrorException( "Internal server error." );
        }

        return new Result( 200 ).render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
    }


    public Result delete( Context context, @Param( "repository" ) String repository, @Param( "md5" ) String md5 )
    {
        checkNotNull( md5, "md5 cannot be null" );

        Integer result;

        try
        {
            //*****************************************************
            UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
            result = templateManagerService.delete( uSession, repository, md5 );
            //*****************************************************
        }
        catch ( IOException e )
        {
            e.printStackTrace();

            throw new InternalServerErrorException( "Error while deleting artifact" );
        }
        switch ( result )
        {
            case 0:
                return Results.ok().render( String.format( "Deleted: %b", result ) ).text();
            case 1:
                return Results.internalServerError().render( "Template was not found" ).text();
            case 2:
                return Results.ok().render( "Not allowed" ).text();
            default:
                return Results.internalServerError().render( "Template was not found" ).text();
        }
    }


    public Result list( Context context, @Param( "repository" ) String repo, @Param( "node" ) String node )
    {
        try
        {
            //*****************************************************
            UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );

            List<SerializableMetadata> defaultTemplateList = templateManagerService.list( uSession, repo, node, false );
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
