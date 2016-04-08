package ai.subut.kurjun.web.controllers;


import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.model.identity.ObjectType;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.web.filter.SecurityFilter;
import ai.subut.kurjun.web.handler.SubutaiFileHandler;
import ai.subut.kurjun.web.model.KurjunFileItem;
import ai.subut.kurjun.web.service.RawManagerService;
import ai.subut.kurjun.web.service.RepositoryService;
import ninja.Context;
import ninja.Renderable;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.session.FlashScope;
import ninja.uploads.FileItem;
import ninja.uploads.FileProvider;

import static com.google.common.base.Preconditions.checkNotNull;


@Singleton
public class RawFileController extends BaseController
{

    @Inject
    private RawManagerService rawManagerService;

    @Inject
    private RepositoryService repositoryService;


    public Result list( Context context , @Param( "repository" ) String repository, @Param( "node" ) String node )
    {
        node = StringUtils.isBlank( node )? "all":node;
        repository = StringUtils.isBlank( repository )? "raw":repository;

        UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
        return Results.html().template( "views/raw-files.ftl" )
                      .render( "files", rawManagerService.list( uSession, repository, node ))
            .render( "repos", repositoryService.getRepositoryContextList( ObjectType.RawRepo.getId() ) )
            .render( "sel_repo", repository ).render( "node", node);
    }


    @FileProvider( SubutaiFileHandler.class )
    public Result upload( Context context, @Param( "file" ) FileItem fileItem, FlashScope flashScope )
    {
        UserSession userSession = ( UserSession ) context.getAttribute( SecurityFilter.USER_SESSION );
        String fingerprint = "raw";


        KurjunFileItem kurjunFileItem = ( KurjunFileItem ) fileItem;

        Metadata metadata;

        metadata = rawManagerService
                .put( userSession, kurjunFileItem.getFile(), kurjunFileItem.getFileName(), fingerprint );

        if ( metadata != null )
        {
            flashScope.success( "Uploaded successfully" );
        }
        else
        {
            flashScope.error( "Failed to upload." );
        }

        return Results.redirect( context.getContextPath() + "/raw-files" );
    }


    public Result download( @PathParam( "id" ) String id )
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
        return Results.text().render( "File not found" );
    }


    public Result delete( Context context, @PathParam( "id" ) String id, FlashScope flashScope )
    {
        checkNotNull( id, "ID cannot be null" );
        String[] temp = id.split( "\\." );

        boolean success = false;

        if ( temp.length == 2 )
        {
            UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
            success = rawManagerService.delete( uSession, temp[0], temp[1] );
        }

        if ( success )
        {
            flashScope.success( "Deleted successfully" );
            return Results.redirect( context.getContextPath() + "/raw-files" );
        }

        flashScope.error( "Failed to delete." );
        return Results.redirect( context.getContextPath() + "/raw-files" );
    }


    public Result md5()
    {
        return Results.ok().render( rawManagerService.md5() ).text();
    }


    public Result info( @Param( "repository" ) String repository, @Param( "md5" ) String md5,
                        @Param( "search" ) String search )
    {
        Metadata metadata = rawManagerService.getInfo( repository, md5, search );

        if ( metadata != null )
        {
            return Results.ok().render( metadata ).json();
        }
        return Results.notFound().render( "Not found" ).text();
    }
}
