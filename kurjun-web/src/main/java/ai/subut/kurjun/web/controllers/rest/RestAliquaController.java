package ai.subut.kurjun.web.controllers.rest;


import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.web.controllers.BaseController;
import ai.subut.kurjun.web.handler.SubutaiFileHandler;
import ai.subut.kurjun.web.model.KurjunFileItem;
import ai.subut.kurjun.web.service.RawManagerService;
import ai.subut.kurjun.web.service.impl.RawManagerServiceImpl;
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


    public Result getFile( Context context, @Param( "repository" ) String repo, @Param( "md5" ) String md5 )
    {
        checkNotNull( md5, "ID cannot be null" );

        Renderable renderable = rawManagerService.getFile( repo, md5 );

        if ( renderable != null )
        {
            return Results.ok().render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
        }
        return Results.notFound().render( "File not found" ).text();
    }


    public Result delete( Context context, @Param( "repository" ) String repo, @Param( "md5" ) String md5 )
    {
        //********************************************
        UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        boolean success = rawManagerService.delete( uSession, repo, md5 );
        //********************************************

        if ( success )
        {
            return Results.ok().render( repo + "." + md5 + " deleted" ).text();
        }

        return Results.notFound().render( "Not found" ).text();
    }


    public Result md5()
    {
        return Results.ok().render( rawManagerService.md5() ).text();
    }


    public Result list( Context context, @Param( "repository" ) String repository, @Param( "node" ) String node )
    {
        node = StringUtils.isBlank( node ) ? "local" : node;
        repository = StringUtils.isBlank( repository ) ? RawManagerServiceImpl.DEFAULT_RAW_REPO_NAME : repository;

        UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        return Results.ok().render( rawManagerService.list( uSession, repository, node ) ).json();
    }


    public Result info( @Param( "repository" ) String repository, @Param( "md5" ) String md5,
                        @Param( "name" ) String name, @Param( "node" ) String node )
    {
        Metadata metadata = rawManagerService.getInfo( repository, md5, name, node );

        if ( metadata != null )
        {
            return Results.ok().render( metadata ).json();
        }
        return Results.notFound().render( "Not found" ).text();
    }
}
